package io.github.leiriad.vibranium.entity;

import com.mojang.datafixers.util.Pair;
import io.github.leiriad.vibranium.block.ReactorCoreBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.init.VibraniumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactorCoreEntity extends BlockEntity {
    //PROPERTIES
    private int temperature = 20; // Ambiant temperature
    private int energyStored = 0;
    private int vibraniumAmount = 0;
    private long waterAmount=0;
    private long hotWaterAmount=0;
    private static final int TICKS_PER_POWDER = 24000;
    private final int MAX_ENERGY = 100000;

    private Map<Pair<Integer, Integer>, List<FluidTankEntity>> waterColumns = new HashMap<>();
    private final List<FluidTankEntity> waterTanks = new ArrayList<>();
    private final List<FluidTankEntity> hotWaterTanks = new ArrayList<>();
    private ReactorHatchEntity cachedHatch = null;

    // CONSTRUCTOR
    public ReactorCoreEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public ReactorCoreEntity(BlockPos pos, BlockState state) {
        this(VibraniumEntities.REACTOR_CORE_ENTITY.get(), pos, state);
    }

    //METHODS
    // Game saving
    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        valueOutput.putInt("energy", energyStored);
        valueOutput.putInt("vibranium", vibraniumAmount);
        valueOutput.putInt("temperature", temperature);
        valueOutput.putLong("water", waterAmount);
        valueOutput.putLong("hot_water", hotWaterAmount);
        super.saveAdditional(valueOutput);
    }
    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.energyStored = valueInput.getInt("energy").orElse(0);
        this.vibraniumAmount = valueInput.getInt("vibranium").orElse(0);
        this.temperature = valueInput.getInt("temperature").orElse(this.temperature);
        this.waterAmount = valueInput.getLong("water").orElse(0l);
        this.hotWaterAmount = valueInput.getLong("hot_water").orElse(0l);
    }

    //Process
    public static void tick(Level level, BlockPos pos, BlockState state, ReactorCoreEntity blockEntity) {
        if (level.isClientSide()) return;
        if (level.getGameTime() % 100 == 0) {
            System.out.println("Eau: " + blockEntity.waterAmount + " | Hatch trouvé: " + (blockEntity.getHatch() != null));
        }
        blockEntity.scanForComponents(level, pos);
        blockEntity.updateFluidLevels();
        if (blockEntity.vibraniumAmount <= 0) {
            blockEntity.refuel();
        }

        boolean hasCoolant = blockEntity.hasCoolant();
        boolean hasFuel = blockEntity.hasFuel();
        //Send heat order
        boolean aFurnaceIsCooking = blockEntity.checkAndBoostAdjacentFurnaces(level, pos, state);

        //Temperature management
        if (hasCoolant && hasFuel) {
            blockEntity.processReaction();
        } else {
            blockEntity.furnaceCoolDown(aFurnaceIsCooking);
        }

    }
    private void scanForComponents(Level level, BlockPos centerPos) {
        this.waterColumns.clear();

        // Scan 3x3x3 cube centered on core
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Pair<Integer, Integer> key = new Pair<>(x, z);

                for (int y = -1; y <= 1; y++) {
                    mutablePos.set(centerPos.getX() + x, centerPos.getY() + y, centerPos.getZ() + z);
                    if (mutablePos.equals(centerPos)) continue;

                    BlockEntity be = level.getBlockEntity(mutablePos);

                    // Fluid tanks
                    if (be instanceof FluidTankEntity tank) {
                        Fluid fluid = tank.getStoredFluid();

                        if (fluid == Fluids.WATER || fluid == VibraniumFluids.HOT_WATER_STILL.get() || fluid == Fluids.EMPTY) {
                            waterColumns.computeIfAbsent(key, k -> new ArrayList<>()).add(tank);
                        }
                    }
                }
            }
        }
        assignRolesToColumns();
    }
    private ReactorHatchEntity getHatch() {
        if (this.cachedHatch == null || this.cachedHatch.isRemoved() || !isNear(this.cachedHatch)) {
            this.cachedHatch = findHatchInWorld();
        }
        return this.cachedHatch;
    }
    private ReactorHatchEntity findHatchInWorld() {
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(this.worldPosition.relative(dir));
            if (be instanceof ReactorHatchEntity hatch) return hatch;
        }
        return null;
    }
    private void assignRolesToColumns() {
        // Clear existing lists to start fresh
        this.waterTanks.clear();
        this.hotWaterTanks.clear();

        // Iterate through all columns identified in the scan
        for (Map.Entry<Pair<Integer, Integer>, List<FluidTankEntity>> entry : waterColumns.entrySet()) {
            List<FluidTankEntity> column = entry.getValue();
            if (column.isEmpty()) continue;

            Fluid detectedFluid = Fluids.EMPTY;
            boolean isPolluted = false;

            // Check for consistency across the column
            for (FluidTankEntity tank : column) {
                Fluid tankFluid = tank.getStoredFluid();

                if (tankFluid != Fluids.EMPTY) {
                    if (detectedFluid == Fluids.EMPTY) {
                        detectedFluid = tankFluid;
                    } else if (detectedFluid != tankFluid) {
                        isPolluted = true; // Fluid mismatch detected in column
                        break;
                    }
                }
            }

            // Ignore polluted columns entirely
            if (isPolluted) {
                continue;
            }

            // Assign valid columns to their respective roles
            if (detectedFluid == Fluids.WATER) {
                waterTanks.addAll(column);
            } else if (detectedFluid == VibraniumFluids.HOT_WATER_STILL.get()) {
                hotWaterTanks.addAll(column);
            } else {
                // Assign empty columns based on what we currently need
                if (waterTanks.isEmpty()) {
                    waterTanks.addAll(column);
                } else {
                    hotWaterTanks.addAll(column);
                }
            }
        }
    }
    private boolean hasCoolant() {
        return this.waterAmount >= 10; //Water consumtion per tick
    }
    private boolean hasFuel() {
        return this.vibraniumAmount > 0;
    }
    private boolean canRefuel() {
        ReactorHatchEntity hatch = getHatch();
        if (hatch == null) return false;
        ItemStack fuelStack = hatch.inventory.getItem(0);
        System.out.println("Le Core voit dans le slot 0 : " + fuelStack.getItem() + " (Quantité: " + fuelStack.getCount() + ")");
        boolean isEmpty = fuelStack.isEmpty();
        boolean isVibraniumDust = fuelStack.getItem() == VibraniumItems.VIBRANIUM_DUST.get();
        return !isEmpty && isVibraniumDust;
    }
    private void refuel() {
        System.out.println("Tentative de refuel...");
        ReactorHatchEntity hatch = getHatch();
        if (hatch == null) {
            System.out.println("ERREUR : Hatch est null !");
            return;
        }
        if (this.canRefuel()) {
            ItemStack fuelStack = hatch.inventory.getItem(0);
            System.out.println("Vibranium détecté : " + fuelStack.getCount());
            fuelStack.shrink(1);
            this.vibraniumAmount = TICKS_PER_POWDER;
            hatch.setChanged();
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }
    private void processReaction() {
        //Check fuel
        if (this.vibraniumAmount <= 0) {
            this.refuel(); // If gauge is empty burn one item
        }
        if (this.vibraniumAmount > 0) {
            this.vibraniumAmount--; // Burn one fuel tick per game tick

            // The reaction in the reactor makes the temperature rise
            if (this.temperature < 1000) {
                this.temperature += 5;
            }

            // Water is consummed
            long waterNeeded = 1;
            for (FluidTankEntity tank : this.waterTanks) {
                long drained = tank.drain(waterNeeded);
                waterNeeded -= drained;
                if (waterNeeded <= 0) break;
            }

            //Reaction produces hot water
            if (waterNeeded <= 0) { // Cold water found
                long waterToProduce = 1;
                for (FluidTankEntity tank : this.hotWaterTanks) {
                    long filled = tank.fill(waterToProduce, VibraniumFluids.HOT_WATER_STILL.get()); // TO DO
                    waterToProduce -= filled;
                    if (waterToProduce <= 0) break;
                }

                if (waterToProduce > 0) {
                    // Emergency procedure: no room left for hat water
                    //this.handlePressureBuildUp();
                }
            } else {
                // Emergency procedure : no cold water left
                //this.handleOverheat();
            }

            // The reactor emits energy
            this.energyStored = Math.min(MAX_ENERGY, this.energyStored + (this.temperature / 10));

            //Signals entity change
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }
    private void updateFluidLevels() {
        this.waterAmount = 0;
        this.hotWaterAmount = 0;

        for (FluidTankEntity tank : this.waterTanks) {
            this.waterAmount += tank.getFluidAmount();
        }
        System.out.println("Eau détectée : " + waterAmount);
        for (FluidTankEntity tank : this.hotWaterTanks) {
            this.hotWaterAmount += tank.getFluidAmount();
        }
        System.out.println("Eau chaude détectée : " + hotWaterAmount);
    }
    public boolean isReactorFunctioningCorrectly() {
        // Check if we have at least one valid water column and one hot water column
        return !waterTanks.isEmpty() && !hotWaterTanks.isEmpty();
    }
    private boolean checkAndBoostAdjacentFurnaces(Level level, BlockPos pos, BlockState state) {
        BlockPos targetPos = pos.relative(state.getValue(ReactorCoreBlock.FACING).getOpposite());
        BlockEntity be = level.getBlockEntity(targetPos);

        if (be instanceof AbstractFurnaceBlockEntity furnace) {
            ItemStack inputStack = furnace.getItem(0); // SLOT_INPUT

            if (!inputStack.isEmpty() && this.temperature > 100) {

                int currentLitTime = furnace.dataAccess.get(0); // 0 = litTimeRemaining
                int currentCookingProgress = furnace.dataAccess.get(2); // 2 = cookingTimer
                int totalCookTime = furnace.dataAccess.get(3); // 3 = cookingTotalTime

                //Fuel the oven with the reactor's heat
                if (currentLitTime < 200) {
                    furnace.dataAccess.set(0, 200);//keep it lit with 10 sec of virtual fuel
                    furnace.dataAccess.set(1, 200); // litTotalTime
                }

                //Overclock the cooking
                if (!furnace.getItem(0).isEmpty() && this.temperature > 100) {
                    // +4 bonus progression per tick makes it cook 5 times quicker
                    int newProgress = Math.min(totalCookTime, currentCookingProgress + 4);
                    furnace.dataAccess.set(2, newProgress);
                }

                //Validate changes
                furnace.setChanged();

                return true; // reactor boosts oven
            }
        }
        return false;
    }
    private void furnaceCoolDown(boolean aFurnaceIsCooking) {
        int targetTemp = 20; // The reactor attempts to reach it's normal temperature

        if (this.temperature > targetTemp) {
            // Oven finishing cooking or off uses residual heat
            int coolingRate = aFurnaceIsCooking ? 4 : 2;
            this.temperature = Math.max(targetTemp, this.temperature - coolingRate);
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }
    public int getMaxVibraniumTicks() { return TICKS_PER_POWDER; }
    private boolean isNear(ReactorHatchEntity hatch) {
        return hatch.getBlockPos().distManhattan(this.worldPosition) <= 2;
    }
    //Screen
    public int getTemperature() {
        System.out.println("this.temperature : " + this.temperature);
        return this.temperature;
    }
    public int getEnergy() {
        System.out.println("this.energyStored : " + this.energyStored);
        return this.energyStored;
    }
    public long getWaterAmount() {
        System.out.println("this.waterAmount : " + this.waterAmount);
        return this.waterAmount;
    }
    public long getHotWaterAmount() {
        System.out.println("this.hotWaterAmount : " + this.hotWaterAmount);
        return this.hotWaterAmount;
    }
    public int getVibraniumAmount() {
        System.out.println("this.vibraniumAmount : " + this.vibraniumAmount);
        System.out.println("Valeur lue côté : " + (level != null && level.isClientSide() ? "CLIENT" : "SERVEUR") + " | Valeur : " + this.vibraniumAmount);
        return this.vibraniumAmount;
    }

    //Server sync
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }
}
