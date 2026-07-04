package io.github.leiriad.vibranium.entity;

import com.mojang.datafixers.util.Pair;
import io.github.leiriad.vibranium.block.ReactorCoreBlock;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.init.VibraniumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class ReactorCoreEntity extends BlockEntity {
    //PROPERTIES
    private int temperature = 20; // Ambiant temperature
    private int energyStored = 0;
    private int vibraniumAmount = 0;
    private long waterAmount=0;
    private long hotWaterAmount=0;
    private final int TICKS_PER_POWDER = 24000;
    private final int MAX_ENERGY = 100000;

    private Map<Pair<Integer, Integer>, List<FluidTankEntity>> waterColumns = new HashMap<>();
    private final List<FluidTankEntity> waterTanks = new ArrayList<>();
    private final List<FluidTankEntity> hotWaterTanks = new ArrayList<>();
    private ReactorHatchEntity cachedHatch = null;
    private boolean structureBlocksValid = false;
    private boolean isStructurePowered = false;

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
        this.energyStored = valueInput.getIntOr("energy",0);
        this.vibraniumAmount = valueInput.getIntOr("vibranium",0);
        this.temperature = valueInput.getIntOr("temperature",this.temperature);
        this.waterAmount = valueInput.getLongOr("water",0L);
        this.hotWaterAmount = valueInput.getLongOr("hot_water",0L);
    }

    //Process
    public static void tick(Level level, BlockPos pos, BlockState state, ReactorCoreEntity blockEntity) {
        if (level.isClientSide()) return;

        blockEntity.scanForComponents(level, pos);
        blockEntity.updateFluidLevels();
        if (blockEntity.vibraniumAmount <= 0) {
            blockEntity.refuel();
        }

        boolean hasCoolant = blockEntity.hasCoolant();
        boolean hasFuel = blockEntity.hasFuel();
        boolean isStructureValid = blockEntity.isReactorFunctioningCorrectly();

        //Send heat order
        boolean aFurnaceIsCooking = blockEntity.checkAndBoostAdjacentFurnaces(level, pos, state);

        //Temperature management
        if (hasFuel && isStructureValid) {
            // The reactor has fuel and a valid structure, it can process
            blockEntity.processReaction(hasCoolant, aFurnaceIsCooking);
        } else {
            // No fuel or invalid setup: standard cooldown due to inactivity
            blockEntity.furnaceCoolDown(aFurnaceIsCooking);
        }
        boolean shouldBeLit = blockEntity.energyStored > 0;
        blockEntity.updateLitState(shouldBeLit);

        //Discharge system
        if (blockEntity.isStructurePowered && blockEntity.energyStored > 0) {
            // Energy evacuation (2000 FE/RF per tick)
            int energyToVanish = Math.min(blockEntity.energyStored, 2000);
            blockEntity.energyStored -= energyToVanish;

            // Visual/sound effect
            if (level.getGameTime() % 10 == 0) {
                level.playSound(null, blockEntity.worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.5F);
                ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE,
                        blockEntity.worldPosition.getX() + 0.5, blockEntity.worldPosition.getY() + 1.1, blockEntity.worldPosition.getZ() + 0.5,
                        5, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }
    public void scanForComponents(Level level, BlockPos centerPos) {
        this.waterColumns.clear();
        this.structureBlocksValid = true; // Assume true, invalidate if an incorrect block is found
        this.isStructurePowered = false;

        // Scan the strict 3x3x3 cube centered on the core
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    // Skip the core itself
                    if (x == 0 && y == 0 && z == 0) continue;

                    mutablePos.set(centerPos.getX() + x, centerPos.getY() + y, centerPos.getZ() + z);
                    BlockState blockState = level.getBlockState(mutablePos);
                    BlockEntity be = level.getBlockEntity(mutablePos);
                    // Check if we have a lever
                    if (level.hasNeighborSignal(mutablePos)) {
                        this.isStructurePowered = true;
                    }

                    // Check if it's a valid functional component (Hatch or Tank)
                    if (be instanceof ReactorHatchEntity || be instanceof FluidTankEntity) {
                        if (be instanceof FluidTankEntity tank) {
                            Fluid fluid = tank.getStoredFluid();
                            if (fluid == Fluids.WATER || fluid == VibraniumFluids.HOT_WATER_STILL.get() || fluid == Fluids.EMPTY) {
                                waterColumns.computeIfAbsent(new Pair<>(x, z), k -> new ArrayList<>()).add(tank);
                            }
                        }
                        continue; // Valid component, move to the next block
                    }

                    // If it's not a BlockEntity component, it MUST be the Reinforced Glass
                    // Replace VibraniumBlocks.REINFORCED_GLASS.get() with your actual block registry object
                    if (!blockState.is(VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get())) {
                        this.structureBlocksValid = false; // Invalid block found in the 3x3x3 shell
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

        // List to keep track of columns that are completely empty
        List<List<FluidTankEntity>> emptyColumns = new ArrayList<>();

        // First Pass: Identify polluted columns, assign defined fluids, and collect empty columns
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

            // Assign valid columns with fluids, or save empty ones for the second pass
            if (detectedFluid == Fluids.WATER) {
                waterTanks.addAll(column);
            } else if (detectedFluid == VibraniumFluids.HOT_WATER_STILL.get()) {
                hotWaterTanks.addAll(column);
            } else {
                // The column is completely empty, defer its assignment
                emptyColumns.add(column);
            }
        }

        // Second Pass: Smartly distribute empty columns based on current counts
        for (List<FluidTankEntity> column : emptyColumns) {
            // Assign to whoever has fewer tanks to keep a balance, prioritizing water if equal
            if (waterTanks.size() <= hotWaterTanks.size()) {
                waterTanks.addAll(column);
            } else {
                hotWaterTanks.addAll(column);
            }
        }
    }
    private boolean hasCoolant() {
        return this.waterAmount >= 1; //Water consumtion per tick
    }
    private boolean hasFuel() {
        return this.vibraniumAmount > 0;
    }
    private boolean canRefuel() {
        ReactorHatchEntity hatch = getHatch();
        if (hatch == null) return false;
        ItemStack fuelStack = hatch.inventory.getItem(0);
        boolean isEmpty = fuelStack.isEmpty();
        boolean isVibraniumDust = fuelStack.getItem() == VibraniumItems.VIBRANIUM_DUST.get();
        return !isEmpty && isVibraniumDust;
    }
    private void refuel() {
        ReactorHatchEntity hatch = getHatch();
        if (hatch == null) {
            return;
        }
        if (this.canRefuel()) {
            ItemStack fuelStack = hatch.inventory.getItem(0);
            fuelStack.shrink(1);
            this.vibraniumAmount = TICKS_PER_POWDER;
            hatch.setChanged();
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }
    private void processReaction(boolean hasCoolant, boolean aFurnaceIsCooking) {
        // GAME DESIGN: If energy buffer is completely full, we safety-stop the reaction (SCRAM)
        // This saves fuel and prevents overheating when the power is not being used.
        if (this.energyStored >= MAX_ENERGY) {
            // The reactor behaves like it's cooling down naturally because no new heat is generated
            this.furnaceCoolDown(aFurnaceIsCooking);
            return;
        }

        if (this.vibraniumAmount <= 0) {
            this.refuel();
        }

        // REACTION STARTS: If there is fuel, the reaction HAPPENS, no matter what
        if (this.vibraniumAmount > 0) {
            this.vibraniumAmount--; // Burn fuel

            // Core reaction always produces energy and heat
            int energyGenerated = 100; // Fixed base production
            this.energyStored = Math.clamp(this.energyStored + energyGenerated, 0, MAX_ENERGY);

            // Base heat generation from fission
            this.temperature += 40;

            // COOLING: Water only acts as a heat sink
            if (hasCoolant) {
                long waterNeeded = 1; // How much water we want to use to cool down
                long actualDrained = 0;

                // Drain water from tanks
                for (FluidTankEntity tank : this.waterTanks) {
                    long drained = tank.drain(waterNeeded - actualDrained);
                    actualDrained += drained;
                    if (actualDrained >= waterNeeded) break;
                }

                // If we successfully used water, reduce temperature and produce hot water
                if (actualDrained > 0) {
                    // Dynamic cooling: Water absorbs a percentage of the CURRENT heat above room temperature
                    int roomTemperature = 20;
                    int heatSurplus = this.temperature - roomTemperature;

                    if (heatSurplus > 0) {
                        // 1 mB of water cools down around 35 degrees instead of instantly freezing the core.
                        // This allows the stable operational temperature to sit around 1100°C - 1300°C.
                        double coolingFactor = 1.0 - (actualDrained * 0.03); // 5 water = 25% heat reduction
                        this.temperature = roomTemperature + (int)(heatSurplus * coolingFactor);
                    }

                    // Convert the used water into hot water
                    long hotWaterToProduce = actualDrained;
                    for (FluidTankEntity tank : this.hotWaterTanks) {
                        long filled = tank.fill(hotWaterToProduce, VibraniumFluids.HOT_WATER_STILL.get());
                        hotWaterToProduce -= filled;
                        if (hotWaterToProduce <= 0) break;
                    }
                }
            } else {
                // MELTDOWN SCENARIO: No coolant means temperature keeps building up exponentially
                if (this.temperature > 3000) {
                    this.handleStepByStepMeltdown(this.level, this.worldPosition);
                }
            }

            // SAVE & SYNC
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }
    public void updateLitState(boolean isEnergyStored) {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState currentState = this.level.getBlockState(this.worldPosition);
            if (currentState.hasProperty(ReactorCoreBlock.LIT) && currentState.getValue(ReactorCoreBlock.LIT) != isEnergyStored) {
                this.level.setBlock(this.worldPosition, currentState.setValue(ReactorCoreBlock.LIT, isEnergyStored), 3);
            }
        }
    }
    public void updateFluidLevels() {
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
        // The reactor is valid only if fluid tanks are present AND the outer shell is made of reinforced glass
        return !waterTanks.isEmpty() && !hotWaterTanks.isEmpty() && this.structureBlocksValid;
    }
    private boolean checkAndBoostAdjacentFurnaces(Level level, BlockPos centerPos, BlockState state) {
        if (level == null) return false;

        boolean atLeastOneFurnaceBoosted = false;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // Scan the 5x5x5 area around the core
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;

                    mutablePos.set(centerPos.getX() + x, centerPos.getY() + y, centerPos.getZ() + z);
                    BlockEntity be = level.getBlockEntity(mutablePos);

                    if (be instanceof AbstractFurnaceBlockEntity furnace) {
                        ItemStack inputStack = furnace.getItem(0); // SLOT_INPUT

                        if (!inputStack.isEmpty()) {
                            boolean isVibranium = inputStack.getItem() == VibraniumItems.VIBRANIUM_DUST.get();

                            // Rule 1: Vibranium demands extreme heat, freeze it if core is too cold
                            if (isVibranium && this.temperature < 1000) {
                                furnace.dataAccess.set(2, 0); // Reset cooking progress
                                furnace.setChanged();
                                continue; // This furnace is not absorbing usable heat right now
                            }

                            // Rule 2: The core must be hot enough (> 100°C) to transfer energy to vanilla items
                            if (this.temperature > 100) {
                                // Provide virtual fuel using the reactor's residual heat
                                int currentLitTime = furnace.dataAccess.get(0);
                                if (currentLitTime < 200) {
                                    furnace.dataAccess.set(0, 200); // 10 seconds buffer
                                    furnace.dataAccess.set(1, 200);
                                }

                                // Overclocking progression calculation
                                int currentCookingProgress = furnace.dataAccess.get(2);
                                int totalCookTime = furnace.dataAccess.get(3);
                                int heatBonus = Math.max(1, this.temperature / 200);


                                // CRITICAL FIX: Cap our custom progress at (totalCookTime - 1).
                                // This allows vanilla furnace tick to add the final +1 and trigger the recipe output.
                                int newProgress = Math.min(totalCookTime - 1, currentCookingProgress + heatBonus);
                                furnace.dataAccess.set(2, newProgress);

                                furnace.setChanged();
                                atLeastOneFurnaceBoosted = true; // Heat is officially being drawn out
                            }
                        }
                    }
                }
            }
        }
        return atLeastOneFurnaceBoosted;
    }
    private void furnaceCoolDown(boolean aFurnaceIsCooking) {
        int targetTemp = 20; // Ambient temperature

        if (this.temperature > targetTemp) {
            boolean temperatureChanged = false;

            if (aFurnaceIsCooking) {
                // Active heat extraction by an adjacent furnace (fast cooldown)
                this.temperature = Math.max(targetTemp, this.temperature - 4);
                temperatureChanged = true;
            } else if (level != null && level.getGameTime() % 40 == 0) {
                // Passive ambient cooling (very slow: 2 degrees every 2 seconds)
                this.temperature = Math.max(targetTemp, this.temperature - 2);
                temperatureChanged = true;
            }

            // If the temperature actually changed, save and sync with the client
            if (temperatureChanged) {
                this.setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                }
            }
        }
    }
    private boolean isNear(ReactorHatchEntity hatch) {
        return hatch.getBlockPos().distManhattan(this.worldPosition) <= 2;
    }
    private void handleStepByStepMeltdown(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return;

        // PHASE 1: Warning Sign (2000°C - 2499°C) -> Structural damage & panic sounds
        if (this.temperature >= 2000 && this.temperature < 2500) {
            if (level.getGameTime() % 20 == 0) { // Every second
                // Play a metallic creaking or warning sound near the core
                level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER,
                        SoundSource.BLOCKS, 0.5F, 2.0F);
            }

            // Spawn heavy smoke particles escaping from the reactor frame
            double px = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 2.0;
            double py = pos.getY() + 1.2;
            double pz = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 2.0;
            ((ServerLevel) level).sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    px, py, pz, 3, 0.0, 0.1, 0.0, 0.02
            );
        }

        // PHASE 2: Containment Breach (2500°C - 2999°C) -> Glass melts into lava, fire spreads
        else if (this.temperature >= 2500 && this.temperature < 3000) {
            if (level.getGameTime() % 10 == 0) {
                // Play alarm sound (Using the Elder Guardian ghost sound for maximum dread)
                level.playSound(null, pos, SoundEvents.ELDER_GUARDIAN_CURSE,
                        SoundSource.BLOCKS, 1.0F, 0.5F);
            }

            // Randomly melt a block of the 3x3x3 outer structure into lava
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            int rx = level.random.nextInt(3) - 1;
            int ry = level.random.nextInt(3) - 1;
            int rz = level.random.nextInt(3) - 1;

            if (!(rx == 0 && ry == 0 && rz == 0)) { // Do not replace the core itself yet
                mutablePos.set(pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz);
                BlockState state = level.getBlockState(mutablePos);

                // If it's reinforced glass or a component, it breaks open!
                if (!state.isAir() && !state.is(Blocks.LAVA)) {
                    level.setBlockAndUpdate(mutablePos, Blocks.LAVA.defaultBlockState());
                    level.levelEvent(2001, mutablePos, Block.getId(state)); // Break particles
                }
            }
        }

        // PHASE 3: Total Core Liquefaction (3000°C+) -> The core explodes and leaves a sea of lava
        else if (this.temperature >= 3000) {
            // Trigger a medium blast to rupture remaining blocks cleanly
            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    4.0F, true, Level.ExplosionInteraction.BLOCK);

            // Turn the entire 3x3x3 core area into a pocket of flowing lava
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        level.setBlockAndUpdate(mutablePos, Blocks.LAVA.defaultBlockState());
                    }
                }
            }

            // Give the Poison effect to any nearby player due to heavy toxic/vibranium fumes
            AABB area = new AABB(pos).inflate(10.0);
            for (Player player : level.getEntitiesOfClass(Player.class, area)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.POISON, 3600, 2 // Poison 2 for 10 seconds
                ));
            }

            // Safely discard the core entity to prevent further tick evaluation
            this.setRemoved();
        }
    }

    //Screen
    public int getTemperature() {
       return this.temperature;
    }
    public int getEnergy() {
        return Math.max(0, this.energyStored);//security to avoid false result due to client server desync
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
    public int getTicksPerPowder(){return  this.TICKS_PER_POWDER;}
    public long getMaxWaterCapacity() {
        long totalCapacity = 0;
        // Map to keep track of columns we already counted (using their unique X/Z key)
        Set<Pair<Integer, Integer>> processedColumns = new HashSet<>();

        for (Map.Entry<Pair<Integer, Integer>, List<FluidTankEntity>> entry : waterColumns.entrySet()) {
            List<FluidTankEntity> column = entry.getValue();
            if (!column.isEmpty() && waterTanks.contains(column.get(0))) {
                // If this column is assigned to water and not processed yet, query its stats
                if (processedColumns.add(entry.getKey())) {
                    totalCapacity += column.get(0).getColumnStats().getSecond();
                }
            }
        }
        return totalCapacity;
    }
    public long getMaxHotWaterCapacity() {
        long totalCapacity = 0;
        // Map to keep track of columns we already counted (using their unique X/Z key)
        Set<Pair<Integer, Integer>> processedColumns = new HashSet<>();

        for (Map.Entry<Pair<Integer, Integer>, List<FluidTankEntity>> entry : waterColumns.entrySet()) {
            List<FluidTankEntity> column = entry.getValue();
            if (!column.isEmpty() && hotWaterTanks.contains(column.get(0))) {
                // If this column is assigned to hot water and not processed yet, query its stats
                if (processedColumns.add(entry.getKey())) {
                    totalCapacity += column.get(0).getColumnStats().getSecond();
                }
            }
        }
        return totalCapacity;
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

    // --- ENERGY MANAGEMENT FOR EXTERNAL MODS COMPATIBILITY ---

    /**
     * Extracts energy from the reactor storage core.
     * @param maxExtract Maximum amount of energy (RF/FE) to extract.
     * @param simulate If true, the extraction will only be simulated.
     * @return The amount of energy actually extracted.
     */
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.energyStored, maxExtract);
        if (!simulate && energyExtracted > 0) {
            this.energyStored -= energyExtracted;
            this.setChanged();
        }
        return energyExtracted;
    }

    /**
     * Gets the current amount of energy stored in the reactor core.
     */
    public int getEnergyStored() {
        return this.energyStored;
    }

    /**
     * Gets the maximum energy capacity of the reactor core.
     */
    public int getMaxEnergyStored() {
        return this.MAX_ENERGY;
    }
}