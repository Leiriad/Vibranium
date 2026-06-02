package io.github.leiriad.vibranium.screen;

import io.github.leiriad.vibranium.menu.ReactorHatchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ReactorHatchScreen extends AbstractContainerScreen<ReactorHatchMenu> implements MenuAccess<ReactorHatchMenu> {
    private static final Identifier FURNACE_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/furnace.png");

    public ReactorHatchScreen(ReactorHatchMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
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
        guiGraphics.blit(
                FURNACE_TEXTURE,
                x, y, x + this.imageWidth, y + this.imageHeight,
                0.0f, 0.0f, (176f / 256f), (166f / 256f)
        );

        //Flame animation
        int remainingTicks = this.menu.getFuelProgress();
        if (remainingTicks > 0) {
            int maxTicks = 24000; //powder lasting
            int mawFlameHeight = 14; //flame height

            // Calculate flame height given the remaining fuel
            int flamePixels = (remainingTicks * mawFlameHeight) / maxTicks;
            if (flamePixels > mawFlameHeight) flamePixels = mawFlameHeight;

            int decalageY = mawFlameHeight - flamePixels
                    ;

            // Lit flame coordinates in oven file : X=176, Y=12, Lenght=14, Height=14
            float u1 = 176f / 256f;
            float v1 = (12f + decalageY) / 256f;
            float u2 = (176f + 14f) / 256f;
            float v2 = (12f + decalageY + flamePixels) / 256f;

            //Draw flame
            guiGraphics.blit(
                    FURNACE_TEXTURE,
                    x + 80, y + 43 + decalageY,
                    x + 80 + 14, y + 43 + decalageY + flamePixels,
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
        //Inventory
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {

            var positioner = net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE;
            Component tooltipText = null;

            // Fuel
            if (this.hoveredSlot.index == 0) {
                tooltipText = Component.translatable("tooltip.vibranium.slot_fuel");
            }
            // Output
            else if (this.hoveredSlot.index == 1) {
                tooltipText = Component.translatable("tooltip.vibranium.slot_output");
            }

            if (tooltipText != null) {
                guiGraphics.renderTooltip(
                        this.font,
                        List.of(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create(tooltipText.getVisualOrderText())),
                        mouseX, mouseY,
                        positioner,
                        null
                );
                return;
            }
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
