package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.ReactorCoreBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class ReactorCoreEntity extends BlockEntity implements WorldlyContainer {
    //PROPERTIES
    private int temperature = 20; // Ambiant temperature
    private int energyStored = 0;
    private int vibraniumAmount = 0;
    private long waterAmount=0;
    private long hotWaterAmount=0;
    private static final int TICKS_PER_POWDER = 24000;
    private final int MAX_ENERGY = 100000;

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

        //Send heat order
        boolean aFurnaceIsCooking = blockEntity.checkAndBoostAdjacentFurnaces(level, pos, state);

        //Temperature management
        if (aFurnaceIsCooking && blockEntity.hasCoolant() && blockEntity.hasFuel()) {
            blockEntity.processReaction();
        } else {
            blockEntity.coolDown(aFurnaceIsCooking);
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
            fuelStack.shrink(1); // On réduit de 1 la quantité dans le slot
            this.vibraniumAmount = TICKS_PER_POWDER; // On recharge la jauge de ticks
            this.setChanged(); // On notifie Minecraft du changement d'inventaire
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

            // Water turns into hot water
            this.waterAmount -= 10;
            this.hotWaterAmount += 10;

            // The reactor emits energy
            this.energyStored = Math.min(MAX_ENERGY, this.energyStored + (this.temperature / 10));

            //Signals entity change
            this.setChanged();
        }
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
        return new int[2];
    }
    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }
    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return false;
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
