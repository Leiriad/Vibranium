package vibranium.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vibranium.init.VibraniumBlocks;

///Allows vibranium_farmland blocks to accept seeds
@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
    @Inject(method="mayPlaceOn", at=@At("HEAD"),cancellable = true)
    private void allowVibraniumFarmland(BlockState state, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Boolean> info){
        if (state.is(VibraniumBlocks.VIBRANIUM_FARMLAND)){
            info.setReturnValue(true);
        }
    }
}
