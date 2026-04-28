package io.github.leiriad.vibranium.client;

import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;


public class VibraniumModClient {

    public static void init() {

            RenderTypeRegistry.register(ChunkSectionLayer.CUTOUT,
                    VibraniumBlocks.BIG_PURPLE_DRIPLEAF,
                    VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM,
                    VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN,
                    VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE,
                    VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET,
                    VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN,
                    VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE,
                    VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET,
                    VibraniumBlocks.PURPLE_CAVE_VINES,
                    VibraniumBlocks.PURPLE_CAVE_VINES_PLANT,
                    VibraniumBlocks.PURPLE_AZALEA,
                    VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH,
                    VibraniumBlocks.FLOWERING_PURPLE_AZALEA,
                    VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH,
                    VibraniumBlocks.PURPLE_VINE,
                    VibraniumBlocks.SMALL_PURPLE_DRIPLEAF,
                    VibraniumBlocks.PURPLE_SHORT_GRASS,
                    VibraniumBlocks.PURPLE_TALL_GRASS,
                    VibraniumBlocks.HEART_SHAPED_HERB
            );

    }
}