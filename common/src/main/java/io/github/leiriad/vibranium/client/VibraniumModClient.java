package io.github.leiriad.vibranium.client;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.init.VibraniumMenus;
import io.github.leiriad.vibranium.screen.ReactorControlPanelScreen;
import io.github.leiriad.vibranium.screen.ReactorHatchScreen;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;


public class VibraniumModClient {
    // A platform-agnostic color cache for your custom renderers to query
    private static final Map<Fluid, Integer> FLUID_COLORS = new HashMap<>();

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
                VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(),
                VibraniumBlocks.REACTOR_HATCH.get(),
                VibraniumBlocks.VIBRANIUM_GLASS.get(),
                VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get(),
                VibraniumBlocks.VIBRANIUM_GLASS_PANE.get(),
                VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS_PANE.get(),
                VibraniumBlocks.FLUID_TANK.get(),
                VibraniumBlocks.REACTOR_CORE.get(),
                VibraniumBlocks.REACTOR_CONTROL_PANEL.get()
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
        MenuScreenRegistry.registerScreenFactory(VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get(), ReactorControlPanelScreen::new);
        MenuScreenRegistry.registerScreenFactory(VibraniumMenus.REACTOR_HATCH_MENU.get(), ReactorHatchScreen::new);

        //Fluid milk for tank
        // Solid Milk white tint mask representation (ARGB hex format)
        int milkColorHex = 0xFFFFFFFF;
        int hotWaterColorHex = 0xFF3F76E4;

        // Register your fluid colors locally inside the common client module
        FLUID_COLORS.put(VibraniumFluids.VANILLA_MILK_STILL.get(), milkColorHex);
        FLUID_COLORS.put(VibraniumFluids.VANILLA_MILK_FLOWING.get(), milkColorHex);
        FLUID_COLORS.put(VibraniumFluids.HOT_WATER_STILL.get(), hotWaterColorHex);
        FLUID_COLORS.put(VibraniumFluids.HOT_WATER_FLOWING.get(), hotWaterColorHex);
    }
    /**
     * Safely retrieves the client-side tint color for any registered fluid.
     * Fallback to full opaque white (0xFFFFFFFF) if not explicitly mapped.
     */
    public static int getFluidColor(Fluid fluid) {
        return FLUID_COLORS.getOrDefault(fluid, 0xFFFFFFFF);
    }
    public static boolean hasColorOverride(Fluid fluid) {
        return FLUID_COLORS.containsKey(fluid);
    }
}