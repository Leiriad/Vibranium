package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.ReactorCoreBlock;
import io.github.leiriad.vibranium.block.ReactorHatchBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class ReactorHatchEntity extends BlockEntity implements WorldlyContainer{
    //PROPERTIES
    public final SimpleContainer inventory = new SimpleContainer(2);
    private BlockPos corePos = null;
    private int trackedVibraniumAmount = 0;
    private int trackedMaxFuelTicks = 24000;

    // CONSTRUCTOR
    public ReactorHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public ReactorHatchEntity(BlockPos pos, BlockState state) {
        this(VibraniumEntities.REACTOR_HATCH_ENTITY.get(), pos, state);
    }

    //METHODS
    public @Nullable ReactorCoreEntity getConnectedCore(LevelAccessor level) {
        if (this.corePos != null) {
            if (level.getBlockEntity(this.corePos) instanceof ReactorCoreEntity reactor) {
                return reactor;
            }
            this.corePos = null;
        }

        //Structure scan-
        int radius = 2;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = this.worldPosition.offset(x, y, z);
                    BlockEntity targetBE = level.getBlockEntity(checkPos);

                    if (targetBE instanceof ReactorCoreEntity reactor) {
                        this.corePos = checkPos;
                        this.setChanged();
                        return reactor;
                    }
                }
            }
        }
        return null;
    }
    public static void tick(Level level, BlockPos pos, BlockState state, ReactorHatchEntity hatch) {
        if (level.isClientSide()) return;

        ReactorCoreEntity core = hatch.getConnectedCore(level);
        hatch.trackedVibraniumAmount = core != null ? core.getVibraniumAmount() : 0;
        if (core != null) {
            hatch.trackedMaxFuelTicks = core.getTicksPerPowder();
        }
        boolean hasFuel = !hatch.inventory.getItem(0).isEmpty();
        hatch.updateLitState(core != null && hasFuel);
    }

    //Inventory Synchronisation
    public ContainerData data = new SimpleContainerData(2) {
        @Override
        public int get(int index) {

            if(index==1){
                return ReactorHatchEntity.this.trackedMaxFuelTicks;
            }
            return ReactorHatchEntity.this.trackedVibraniumAmount;
        }
        @Override
        public void set(int index, int value) {
            if(index==0){
                ReactorHatchEntity.this.trackedVibraniumAmount = value;
            }
        }
        @Override
        public int getCount() { return 2; }
    };

    //Game saving
    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        if (this.corePos != null) {
            valueOutput.putInt("coreX", this.corePos.getX());
            valueOutput.putInt("coreY", this.corePos.getY());
            valueOutput.putInt("coreZ", this.corePos.getZ());
            valueOutput.putBoolean("hasCore", true);
        } else {
            valueOutput.putBoolean("hasCore", false);
        }
        valueOutput.store("Inventory", ItemStack.OPTIONAL_CODEC.listOf(), this.inventory.getItems());
        super.saveAdditional(valueOutput);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        if (valueInput.getBooleanOr("hasCore", false)) {
            int x = valueInput.getInt("coreX").orElse(0);
            int y = valueInput.getInt("coreY").orElse(0);
            int z = valueInput.getInt("coreZ").orElse(0);
            this.corePos = new BlockPos(x, y, z);
        }
        valueInput.read("Inventory", ItemStack.OPTIONAL_CODEC.listOf()).ifPresent(items -> {
            for (int i = 0; i < items.size() && i < this.inventory.getContainerSize(); i++) {
                this.inventory.setItem(i, items.get(i));
            }
        });
    }

    public void updateLitState(boolean isFuelPresent) {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState currentState = this.level.getBlockState(this.worldPosition);
            if (currentState.hasProperty(ReactorHatchBlock.LIT) && currentState.getValue(ReactorHatchBlock.LIT) != isFuelPresent) {
                this.level.setBlock(this.worldPosition, currentState.setValue(ReactorHatchBlock.LIT, isFuelPresent), 3);
            }
        }
    }

    //Worldly Container
    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && stack.is(VibraniumItems.VIBRANIUM_DUST);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }

    @Override
    public int getContainerSize() { return inventory.getContainerSize(); }
    @Override
    public boolean isEmpty() { return inventory.isEmpty(); }
    @Override
    public ItemStack getItem(int slot) { return inventory.getItem(slot); }
    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack res = inventory.removeItem(slot, amount);
        this.setChanged();
        return res;
    }
    @Override
    public ItemStack removeItemNoUpdate(int slot) { return inventory.removeItemNoUpdate(slot); }
    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
        this.setChanged();
    }
    @Override
    public boolean stillValid(Player player) { return true; }
    @Override
    public void clearContent() { inventory.clearContent(); }
}
