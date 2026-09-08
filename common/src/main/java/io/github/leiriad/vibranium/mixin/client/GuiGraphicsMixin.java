package io.github.leiriad.vibranium.mixin.client;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL")
    )
    private void vibranium$renderKineticChargeBar(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (stack.isEmpty()) return;

        // Retrieve charge amount
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        if (charge <= 0.0F) return;

        GuiGraphics graphics = (GuiGraphics) (Object) this;

        // Calculate bar width (0 to 13 pixels)
        int barWidth = Math.round((Math.min(charge, 100.0F) / 100.0F) * 13.0F);

        // Charge bar position:
        // If the item is damaged, the Vanilla durability bar is rendered at y + 13.
        // We place the charge bar directly above it (y + 11).
        // If the item is undamaged, render it at the standard position (y + 13).
        int barY = stack.isDamaged() ? y + 11 : y + 13;
        int barX = x + 2;

        //Background for the bar (13x2 pixels)
        graphics.fill(barX, barY, barX + 13, barY + 2, VibraniumMod.BarBackground);

        //Charge overlay (barWidth x 1 pixel)
        graphics.fill(barX, barY, barX + barWidth, barY + 1, VibraniumMod.BarColour);
    }
}