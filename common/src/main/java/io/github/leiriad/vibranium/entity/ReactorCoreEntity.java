package io.github.leiriad.vibranium.entity;

import com.mojang.datafixers.util.Pair;
import io.github.leiriad.vibranium.block.ReactorCoreBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.init.VibraniumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class ReactorCoreEntity extends BlockEntity implements WorldlyContainer {
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
    private ReactorHatchEntity hatch = null;

    //Inventory
    public final SimpleContainer inventory = new SimpleContainer(2);
    private static final int[] SLOTS_FOR_INPUT = new int[]{0}; //Vibranium dust input
    private static final int[] SLOTS_FOR_OUTPUT = new int[]{1}; //Depleted Vibranium

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
        valueOutput.store("Inventory", ItemStack.OPTIONAL_CODEC.listOf(), this.inventory.getItems());
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
        valueInput.read("Inventory", ItemStack.OPTIONAL_CODEC.listOf()).ifPresent(items -> {
            this.inventory.clearContent();
            for (int i = 0; i < items.size() && i < this.inventory.getContainerSize(); i++) {
                this.inventory.setItem(i, items.get(i));
            }
        });
    }

    //Process
    public static void tick(Level level, BlockPos pos, BlockState state, ReactorCoreEntity blockEntity) {
        if (level.isClientSide()) return;
        blockEntity.scanForComponents(level, pos);
        blockEntity.updateFluidLevels();
        //Send heat order
        boolean aFurnaceIsCooking = blockEntity.checkAndBoostAdjacentFurnaces(level, pos, state);

        //Temperature management
        if (aFurnaceIsCooking && blockEntity.hasCoolant() && blockEntity.hasFuel()) {
            blockEntity.processReaction();
        } else {
            blockEntity.furnaceCoolDown(aFurnaceIsCooking);
        }

    }
    private void scanForComponents(Level level, BlockPos centerPos) {
        this.waterColumns.clear();
        this.hatch = null;

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
                    // Hatch
                    else if (be instanceof ReactorHatchEntity hatch) {
                        this.hatch = hatch;
                    }
                }
            }
        }
        assignRolesToColumns();
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
        return this.vibraniumAmount > 0 || this.canRefuel();
    }
    private boolean canRefuel() {
        ItemStack fuelStack = this.inventory.getItem(0);
        return !fuelStack.isEmpty() && fuelStack.is(VibraniumItems.VIBRANIUM_DUST);
    }
    private void refuel() {
        if (this.canRefuel()) {
            ItemStack fuelStack = this.inventory.getItem(0);
            fuelStack.shrink(1); // Reduce quantity
            this.vibraniumAmount = TICKS_PER_POWDER; // Change gauge
            this.setChanged();
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
            long waterNeeded = 10;
            for (FluidTankEntity tank : this.waterTanks) {
                long drained = tank.drain(waterNeeded);
                waterNeeded -= drained;
                if (waterNeeded <= 0) break;
            }

            //Reaction produces hot water
            if (waterNeeded <= 0) { // Cold water found
                long waterToProduce = 10;
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
        }
    }
    private void updateFluidLevels() {
        this.waterAmount = 0;
        this.hotWaterAmount = 0;

        for (FluidTankEntity tank : this.waterTanks) {
            this.waterAmount += tank.getFluidAmount();
        }
        for (FluidTankEntity tank : this.hotWaterTanks) {
            this.hotWaterAmount += tank.getFluidAmount();
        }
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
        }
    }
    public void addWater(long amount) {
        this.waterAmount = Math.min(10000L, this.waterAmount + amount); // Max capacity 10 buckets
    }
    public int getMaxVibraniumTicks() { return TICKS_PER_POWDER; }

    //Screen
    public int getTemperature() {
        return this.temperature;
    }
    public int getEnergy() {
        return this.energyStored;
    }
    public long getWaterAmount() {
        return this.waterAmount;
    }
    public long getHotWaterAmount() {
        return this.hotWaterAmount;
    }
    public int getVibraniumAmount() {
        return this.vibraniumAmount;
    }

    //Worldly Container
    @Override
    public int[] getSlotsForFace(Direction direction) {
        // Returns every available slot for every face
        return new int[]{SLOTS_FOR_INPUT[0], SLOTS_FOR_OUTPUT[0]};
    }
    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        //Allows insertion in vibranium slot
        return slot == SLOTS_FOR_INPUT[0];
    }
    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        //Allows insertion in waste slot
        return slot == SLOTS_FOR_OUTPUT[0];
    }
    @Override
    public int getContainerSize() {
        return this.inventory.getContainerSize();
    }
    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }
    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.getItem(slot);
    }
    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = this.inventory.removeItem(slot, amount);
        this.setChanged();
        return stack;
    }
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.inventory.removeItemNoUpdate(slot);
    }
    @Override
    public void setItem(int slot, ItemStack itemStack) {
        this.inventory.setItem(slot, itemStack);
        this.setChanged();
    }
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    @Override
    public void clearContent() {
        this.inventory.clearContent();
    }

}
