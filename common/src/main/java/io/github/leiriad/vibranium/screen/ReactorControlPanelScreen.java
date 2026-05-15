package io.github.leiriad.vibranium.screen;

import io.github.leiriad.vibranium.menu.ReactorControlPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ReactorControlPanelScreen extends AbstractContainerScreen<ReactorControlPanelMenu> implements MenuAccess<ReactorControlPanelMenu> {
    private static final Identifier VANILLA_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/dispenser.png");

    public ReactorControlPanelScreen(ReactorControlPanelMenu menu, Inventory inv, Component title) {
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
        //Identifier texture = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "textures/gui/reactor_control_panel.png");

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
                VANILLA_TEXTURE,
                x, y,
                this.imageWidth, this.imageHeight,
                0.0f, 0.0f,
                (float)this.imageWidth,
                (float)this.imageHeight );
    }
}
