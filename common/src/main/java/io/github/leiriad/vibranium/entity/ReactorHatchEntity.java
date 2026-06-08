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

public class ReactorHatchEntity extends BlockEntity{
    //PROPERTIES
    private BlockPos corePos = null;

    // CONSTRUCTOR
    public ReactorHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public ReactorHatchEntity(BlockPos pos, BlockState state) {
        this(VibraniumEntities.REACTOR_HATCH_ENTITY.get(), pos, state);
    }

    //METHODS
    public @Nullable WorldlyContainer getConnectedCore(LevelAccessor level) {
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
    }

    public void updateLitState(boolean isFuelPresent) {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState currentState = this.level.getBlockState(this.worldPosition);
            if (currentState.hasProperty(ReactorHatchBlock.LIT) && currentState.getValue(ReactorHatchBlock.LIT) != isFuelPresent) {
                this.level.setBlock(this.worldPosition, currentState.setValue(ReactorHatchBlock.LIT, isFuelPresent), 3);
            }
        }
    }
}
