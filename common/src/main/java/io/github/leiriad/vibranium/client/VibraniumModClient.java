package io.github.leiriad.vibranium.client;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;


public class VibraniumModClient {

    public static void init() {

            RenderTypeRegistry.register(ChunkSectionLayer.CUTOUT,
                    VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(),
                    VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(),
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
                    VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(),
                    VibraniumBlocks.PURPLE_SHORT_GRASS.get(),
                    VibraniumBlocks.PURPLE_TALL_GRASS.get(),
                    VibraniumBlocks.HEART_SHAPED_HERB.get()
            );
        //vine color fix
        ColorHandlerRegistry.registerBlockColors(
                (state, world, pos, tintIndex) -> 0xFFFFFF,
                VibraniumBlocks.PURPLE_VINE
        );
    }
}