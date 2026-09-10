package io.github.leiriad.vibranium.mixin.client;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.config.VibraniumConfigManager;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Gui.class)
public abstract class GuiHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void vibranium$renderKineticHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        // Do not render HUD if player is null, GUI is hidden (F1 mode), or spectator
        if (player == null || client.options.hideGui || player.isSpectator()) {
            return;
        }

        // Check equipped chestplate
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.isEmpty()) {
            return;
        }

        // Retrieve current charge percentage from data component
        float charge = chestStack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        if (charge <= 0.0F) {
            return;
        }

        renderKineticTopBar(guiGraphics, client, charge);
    }

    /**
     * Renders a top-centered kinetic charge bar safely below active boss bars.
     */
    private void renderKineticTopBar(GuiGraphics graphics, Minecraft client, float charge) {
        int screenWidth = client.getWindow().getGuiScaledWidth();

        // Bar dimensions
        int barWidth = 100;
        int barHeight = 4;

        //Bar locations
        String alignment = VibraniumConfigManager.INSTANCE.client.hudAlignment;

        int padding = 10; //From left border
        int barX;

        switch (alignment.toUpperCase()) {
            case "LEFT" -> barX = padding;
            case "RIGHT" -> barX = screenWidth - barWidth - padding;
            default -> barX = (screenWidth - barWidth) / 2; // CENTER
        }
        // Retrieve active boss bars safely without shadow fields
        int activeBossBars = 0;
        if (client.gui != null && client.gui.getBossOverlay() != null) {
            Map<?, ?> events = ((BossHealthOverlayAccessor) client.gui.getBossOverlay()).vibranium$getEvents();
            if (events != null) {
                activeBossBars = events.size();
            }
        }

        // Calculate Y position (19px offset per active boss bar)
        int topMargin = 12;
        boolean isSideAligned = alignment.equalsIgnoreCase("LEFT") || alignment.equalsIgnoreCase("RIGHT");

        int startY = isSideAligned
                ? topMargin
                : topMargin + (activeBossBars * 19);


        //Fill bar
        int fillWidth = Math.round((Math.min(charge, 100.0F) / 100.0F) * barWidth);

        // Outer black border
        graphics.fill(barX - 1, startY - 1, barX + barWidth + 1, startY + barHeight + 1, 0xFF000000);

        // Inner background
        graphics.fill(barX, startY, barX + barWidth, startY + barHeight, VibraniumMod.BarBackground);

        // Charge fill
        if (fillWidth > 0) {
            graphics.fill(barX, startY, barX + fillWidth, startY + barHeight, VibraniumMod.BarColour);
        }
    }
}