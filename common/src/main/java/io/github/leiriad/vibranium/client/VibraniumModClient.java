package io.github.leiriad.vibranium.client;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumMenus;
import io.github.leiriad.vibranium.screen.ReactorControlPanelScreen;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.registries.BuiltInRegistries;


public class VibraniumModClient {

    public static void init() {

        RenderTypeRegistry.register(ChunkSectionLayer.CUTOUT,
                    VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(),
                    VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(),
                    VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(),
                    VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(),
                    VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(),
                    VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(),
                    VibraniumBlocks.PURPLE_CAVE_VINES.get(),
                    VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(),
                    VibraniumBlocks.PURPLE_AZALEA.get(),
                    VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(),
                    VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(),
                    VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(),
                    VibraniumBlocks.PURPLE_VINE.get(),
                    VibraniumBlocks.PURPLE_SHORT_GRASS.get(),
                    VibraniumBlocks.PURPLE_TALL_GRASS.get(),
                    VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get()
        );
        RenderTypeRegistry.register(ChunkSectionLayer.TRANSLUCENT,
                VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(),
                VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(),
                VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get()
        );

        //Vine & dripleaf color fix
        ColorHandlerRegistry.registerBlockColors(
                (state, world, pos, tintIndex) -> 0xFFFFFF,
                VibraniumBlocks.PURPLE_VINE
        );
        ColorHandlerRegistry.registerBlockColors(
                (state, world, pos, tintIndex) -> 0xFFFFFF,
                VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get()
        );

        //Screens init
        System.out.println("-> ENREGISTREMENT DE L'ÉCRAN DU PANNEAU !");
        var menuType = VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get();
        System.out.println("-> Tentative d'enregistrement pour l'ID : " + BuiltInRegistries.MENU.getKey(menuType));
        MenuScreenRegistry.registerScreenFactory(VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get(), ReactorControlPanelScreen::new);

    }
}