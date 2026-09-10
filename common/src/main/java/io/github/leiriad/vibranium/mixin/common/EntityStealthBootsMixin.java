package io.github.leiriad.vibranium.mixin.common;

import io.github.leiriad.vibranium.item.BlackPantherArmorItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityStealthBootsMixin {

    @Inject(method = "dampensVibrations", at = @At("HEAD"), cancellable = true)
    private void vibranium$suppressBootsVibrations(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof LivingEntity living) {
            ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
            if (boots.getItem() instanceof BlackPantherArmorItem) {
                cir.setReturnValue(true);
            }
        }
    }
}