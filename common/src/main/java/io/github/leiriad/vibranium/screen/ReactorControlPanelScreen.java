package io.github.leiriad.vibranium.screen;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.menu.ReactorControlPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ReactorControlPanelScreen extends AbstractContainerScreen<ReactorControlPanelMenu> implements MenuAccess<ReactorControlPanelMenu> {
    Identifier texture = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "textures/gui/reactor_control_panel_screen_gui.png");

    public ReactorControlPanelScreen(ReactorControlPanelMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 1920;
        this.imageHeight = 1080;
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Menu position
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        //Menu design
        /*guiGraphics.blit(
                texture,
                x, y,                         // Position
                this.imageWidth, this.imageHeight, // Size
                0.0f, 0.0f,                   // UV Coordinates (f,g) in the png file
                (float)this.imageWidth,       // Width to cut (h)
                (float)this.imageHeight       // Height to cup (m)
        );*/
        guiGraphics.blit(
                texture,
                x, y, x + this.imageWidth, y + this.imageHeight,
                0.0f, 0.0f, (176f / 256f), (166f / 256f)
        );

        //Gauges
        int maxHeight = 60;
        //Energy (Max 100000)
        drawVerticalGauge(guiGraphics, x + 25, y + 40, this.menu.getEnergy(), 100000, maxHeight, 0, 0, 10);

        //Heat (Max 1000)
        drawVerticalGauge(guiGraphics, x + 55, y + 40, this.menu.getHeat(), 1000, maxHeight, 0, 0, 10);

        //Water (Max 10000)
        drawVerticalGauge(guiGraphics, x + 85, y + 40, this.menu.getWater(), 10000, maxHeight, 0, 0, 10);

        //Hot Water (Max 10000)
        drawVerticalGauge(guiGraphics, x + 115, y + 40, this.menu.getHotWater(), 10000, maxHeight, 0, 0, 10);

        //Vibranium (Burn ticks left, max 24000)
        drawVerticalGauge(guiGraphics, x + 145, y + 40, this.menu.getVibranium(), 24000, maxHeight, 0, 0, 10);
    }
    private void drawVerticalGauge(GuiGraphics guiGraphics, int x, int y, long currentValue, long maxValue, int maxHeight, int texturePixelX, int texturePixelY, int width) {
        if (maxValue <= 0) return;

        // Calculation is done using longs to prevent any scaling or overflow issues
        int renderedPixels = (int) ((currentValue * maxHeight) / maxValue);
        if (renderedPixels > maxHeight) renderedPixels = maxHeight;

        int yOffset = maxHeight - renderedPixels;

        // Conversion des coordonnées pixels en FLOAT pour l'UV mapping (sur une texture de 256x256)
        float u1 = texturePixelX / 256.0f;
        float v1 = (texturePixelY + yOffset) / 256.0f;
        float u2 = (texturePixelX + width) / 256.0f;
        float v2 = (texturePixelY + yOffset + renderedPixels) / 256.0f;
        if (renderedPixels > 0) {
            guiGraphics.blit(
                    texture,
                    x, y + yOffset,
                    x + width, y + yOffset + renderedPixels,
                    u1, v1, u2, v2
            );
        }
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int labelY = 28;
        int color = 4210752;

        // Title
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        //Start position of the menu
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int jaugeTop = y + 40;
        int jaugeBottom = y + 40 + 60;
        int jaugeLargeur = 10;

        var positioner = net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE;

        //Energy
        if (mouseX >= x + 25 && mouseX < x + 25 + jaugeLargeur && mouseY >= jaugeTop && mouseY < jaugeBottom) {
            Component text = Component.translatable("gui.vibranium.energy_tooltip", this.menu.getEnergy());
            guiGraphics.renderTooltip(
                    this.font,
                    List.of(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create(text.getVisualOrderText())),
                    mouseX, mouseY,
                    positioner,
                    null ///TO DO: Replace by new texture
            );
        }

        //Heat
        if (mouseX >= x + 55 && mouseX < x + 55 + jaugeLargeur && mouseY >= jaugeTop && mouseY < jaugeBottom) {
            Component text = Component.translatable("gui.vibranium.heat_tooltip", this.menu.getHeat());
            guiGraphics.renderTooltip(
                    this.font,
                    List.of(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create(text.getVisualOrderText())),
                    mouseX, mouseY,
                    positioner,
                    null
            );
        }

        //Water
        if (mouseX >= x + 85 && mouseX < x + 85 + jaugeLargeur && mouseY >= jaugeTop && mouseY < jaugeBottom) {
            Component text = Component.translatable("gui.vibranium.water_tooltip", this.menu.getWater());
            guiGraphics.renderTooltip(
                    this.font,
                    List.of(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create(text.getVisualOrderText())),
                    mouseX, mouseY,
                    positioner,
                    null
            );
        }

        //Hot water
        if (mouseX >= x + 115 && mouseX < x + 115 + jaugeLargeur && mouseY >= jaugeTop && mouseY < jaugeBottom) {
            Component text = Component.translatable("gui.vibranium.hot_water_tooltip", this.menu.getHotWater());
            guiGraphics.renderTooltip(
                    this.font,
                    List.of(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create(text.getVisualOrderText())),
                    mouseX, mouseY,
                    positioner,
                    null
            );
        }

        //Vibranium
        //Percentage or time
        if (mouseX >= x + 145 && mouseX < x + 145 + jaugeLargeur && mouseY >= jaugeTop && mouseY < jaugeBottom) {
            int secondsRemaining = this.menu.getVibranium() / 20;
            Component text = Component.translatable("gui.vibranium.fuel_tooltip", secondsRemaining);
            guiGraphics.renderTooltip(
                    this.font,
                    List.of(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create(text.getVisualOrderText())),
                    mouseX, mouseY,
                    positioner,
                    null
            );
        }
    }
}
