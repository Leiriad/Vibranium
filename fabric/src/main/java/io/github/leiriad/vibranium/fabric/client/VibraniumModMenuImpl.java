package io.github.leiriad.vibranium.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.gui.screens.Screen;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class VibraniumModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // We pass the method reference of your Fabric-specific screen here.
        // Mod Menu will automatically call this and provide the current screen as the parent.
        return parent -> FabricVibraniumScreen.create(parent);
    }
}
