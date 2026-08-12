package io.github.leiriad.vibranium.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.menu.ReactorControlPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ReactorControlPanelScreen extends AbstractContainerScreen<ReactorControlPanelMenu> implements MenuAccess<ReactorControlPanelMenu> {
    private final Identifier texture = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "textures/gui/reactor_control_panel_screen_gui.png");

    public ReactorControlPanelScreen(ReactorControlPanelMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Menu position
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2 + 5;

        // Menu design
        guiGraphics.blit(
                net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                texture,
                x, y,
                0.0f, 0.0f,
                this.imageWidth, this.imageHeight,
                256, 256,
                0xFFFFFFFF
        );

        // Gauges
        int maxHeight = 78;
        // Energy (Max 100000)
        drawVerticalGauge(guiGraphics, x + 44, y + 77, this.menu.getEnergy(), 100000, maxHeight, 0, 0, 14, GaugeType.ENERGY);

        // Heat (Max 3000)
        drawVerticalGauge(guiGraphics, x + 83, y + 77, this.menu.getHeat(), 2500, maxHeight, 0, 0, 14, GaugeType.HEAT);

        // Water (Max 10000)
        drawVerticalGauge(guiGraphics, x + 121, y + 77, this.menu.getWater(), this.menu.getMaxWater(), maxHeight, 0, 0, 14, GaugeType.WATER);

        // Hot Water (Max 10000)
        drawVerticalGauge(guiGraphics, x + 160, y + 77, this.menu.getHotWater(), this.menu.getMaxHotWater(), maxHeight, 0, 0, 14, GaugeType.HOT_WATER);

        // Vibranium (Burn ticks left, max 24000)
        drawVerticalGauge(guiGraphics, x + 199, y + 77, this.menu.getVibranium(), 24000, maxHeight, 0, 0, 14, GaugeType.VIBRANIUM);
    }

    private void drawVerticalGauge(GuiGraphics guiGraphics, int x, int y, long currentValue, long maxValue, int maxHeight, int texturePixelX, int texturePixelY, int width, GaugeType gaugeType) {
        if (maxValue <= 0) return;

        // Choose texture according to gauge type
        Identifier gaugeTexture = (gaugeType == GaugeType.WATER || gaugeType == GaugeType.VIBRANIUM)
                ? Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "textures/gui/reactor_control_panel_screen_gui_gauge_inverted.png")
                : Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "textures/gui/reactor_control_panel_screen_gui_gauge.png");

        // Calculation is done using longs to prevent any scaling or overflow issues
        int renderedPixels = (int) ((currentValue * maxHeight) / maxValue);
        if (renderedPixels > maxHeight) renderedPixels = maxHeight;

        int yOffset = maxHeight - renderedPixels;

        if (renderedPixels > 0) {
            guiGraphics.blit(
                    net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                    gaugeTexture,
                    x, y + yOffset,
                    (float) texturePixelX, (float) (texturePixelY + yOffset),
                    width, renderedPixels,
                    13, 78,
                    0xFFFFFFFF
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Title
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        // Start position of the menu (Must exactly match renderBg, including the +5 offset)
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2 + 5;

        int gaugeTop = y + 77;
        int gaugeBottom = y + 77 + 78;
        int gaugeWidth = 14;

        var positioner = DefaultTooltipPositioner.INSTANCE;

        // Energy (Rendered at x + 44)
        if (mouseX >= x + 44 && mouseX < x + 44 + gaugeWidth && mouseY >= gaugeTop && mouseY < gaugeBottom) {
            Component text = Component.translatable("gui." + VibraniumMod.MOD_ID + ".energy_tooltip", this.menu.getEnergy());
            guiGraphics.renderTooltip(this.font, List.of(ClientTooltipComponent.create(text.getVisualOrderText())), mouseX, mouseY, positioner, null);
        }

        // Heat (Rendered at x + 83)
        if (mouseX >= x + 83 && mouseX < x + 83 + gaugeWidth && mouseY >= gaugeTop && mouseY < gaugeBottom) {
            Component text = Component.translatable("gui." + VibraniumMod.MOD_ID + ".heat_tooltip", this.menu.getHeat());
            guiGraphics.renderTooltip(this.font, List.of(ClientTooltipComponent.create(text.getVisualOrderText())), mouseX, mouseY, positioner, null);
        }

        // Water (Rendered at x + 121)
        if (mouseX >= x + 121 && mouseX < x + 121 + gaugeWidth && mouseY >= gaugeTop && mouseY < gaugeBottom) {
            Component text = Component.translatable("gui." + VibraniumMod.MOD_ID + ".water_tooltip", this.menu.getWater(), this.menu.getMaxWater());
            guiGraphics.renderTooltip(this.font, List.of(ClientTooltipComponent.create(text.getVisualOrderText())), mouseX, mouseY, positioner, null);
        }

        // Hot Water (Rendered at x + 160)
        if (mouseX >= x + 160 && mouseX < x + 160 + gaugeWidth && mouseY >= gaugeTop && mouseY < gaugeBottom) {
            Component text = Component.translatable("gui." + VibraniumMod.MOD_ID + ".hot_water_tooltip", this.menu.getHotWater(), this.menu.getMaxHotWater());
            guiGraphics.renderTooltip(this.font, List.of(ClientTooltipComponent.create(text.getVisualOrderText())), mouseX, mouseY, positioner, null);
        }

        // Vibranium (Rendered at x + 199)
        if (mouseX >= x + 199 && mouseX < x + 199 + gaugeWidth && mouseY >= gaugeTop && mouseY < gaugeBottom) {
            int secondsRemaining = this.menu.getVibranium() / 20;
            Component text = Component.translatable("gui." + VibraniumMod.MOD_ID + ".fuel_tooltip", secondsRemaining);
            guiGraphics.renderTooltip(this.font, List.of(ClientTooltipComponent.create(text.getVisualOrderText())), mouseX, mouseY, positioner, null);
        }
    }

    public enum GaugeType {
        ENERGY, HEAT, WATER, HOT_WATER, VIBRANIUM
    }
}