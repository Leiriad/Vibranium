package io.github.leiriad.vibranium.mixin.common;

import io.github.leiriad.vibranium.item.BlackPantherArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerStepSoundMixin {

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void vibranium$muteStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        //Cancels player step sound if player wears black panther boots
        if (player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof BlackPantherArmorItem) {
            ci.cancel();
        }
    }
}