package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.ReactorCoreBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ReactorCoreEntity extends BlockEntity {
    //PROPERTIES
    private int temperature = 20; // Ambiante
    private int energyStored = 0;
    private final int MAX_ENERGY = 100000;
    public final SimpleContainer inventory = new SimpleContainer(2);
    private long waterAmount=0;
    private long hotWaterAmount=0;

    public ReactorCoreEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // CONSTRUCTOR
    public ReactorCoreEntity(BlockPos pos, BlockState state) {
        this(VibraniumEntities.REACTOR_CORE_ENTITY.get(), pos, state);
    }

    //METHODS
    private boolean hasCoolant() {
        return this.waterAmount >= 10; //Water consumtion per tick
    }
    // Game saving
    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        valueOutput.putInt("energy", energyStored);
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

        // 1. On vérifie l'état des fours adjacents et on récupère la demande en chaleur
        boolean aFurnaceIsCooking = blockEntity.checkAndBoostAdjacentFurnaces(level, pos, state);

        // 2. Gestion de la température
        if (aFurnaceIsCooking && blockEntity.hasCoolant()) {
            // Si les fours fonctionnent et qu'on a de l'eau, le réacteur produit de l'énergie et de la chaleur
            blockEntity.processReaction();
        } else {
            // Sinon (pas de four actif, ou plus d'eau), le réacteur refroidit
            // On lui passe le paramètre 'aFurnaceIsCooking' car si un four est chaud mais non alimenté, 
            // il peut dissiper la chaleur du réacteur plus vite !
            blockEntity.coolDown(aFurnaceIsCooking);
        }
    }

    private void processReaction() {
        // The reaction in the reactor makes the temperature rise
        if (this.temperature < 1000) {
            this.temperature += 5;
        }

        // Water turns into hot water
        this.waterAmount -= 10;
        this.hotWaterAmount += 10;

        // The reactor emits energy
        this.energyStored += (this.temperature / 10);
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

    private void coolDown(boolean aFurnaceIsCooking) {
        int targetTemp = 20; // The reactor attempts to reach it's normal temperature

        if (this.temperature > targetTemp) {
            // Oven finishing cooking or off uses residual heat
            int coolingRate = aFurnaceIsCooking ? 4 : 2;

            this.temperature = Math.max(targetTemp, this.temperature - coolingRate);
        }
    }

    public int getTemperature() {
        return this.temperature;
    }

    public int getEnergyStored() {
        return this.energyStored;
    }

    public long getWaterAmount() {
        return this.waterAmount;
    }

    public long getHotWaterAmount() {
        return this.hotWaterAmount;
    }

    public void addWater(long amount) {
        this.waterAmount = Math.min(10000L, this.waterAmount + amount); // Max capacity 10 buckets
    }
}
