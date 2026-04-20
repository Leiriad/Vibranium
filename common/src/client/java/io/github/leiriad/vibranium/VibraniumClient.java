package vibranium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

public class VibraniumClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		// Dans ta méthode onInitializeClient
		BlockRenderLayerMap.putBlock(VibraniumBlocks.VIBRANIUM_ORE, ChunkSectionLayer.SOLID);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.VIBRANIUM_DIRT, ChunkSectionLayer.SOLID);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, ChunkSectionLayer.SOLID);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.VIBRANIUM_PATH, ChunkSectionLayer.SOLID);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.VIBRANIUM_FARMLAND, ChunkSectionLayer.SOLID);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.BLACKGRAVEL, ChunkSectionLayer.SOLID);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.BLACKCLAY, ChunkSectionLayer.SOLID);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_SHORT_GRASS, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_TALL_GRASS, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_AZALEA, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.FLOWERING_PURPLE_AZALEA, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_MOSS_BLOCK, ChunkSectionLayer.SOLID);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_MOSS_CARPET, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_CAVE_VINES, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.BIG_PURPLE_DRIPLEAF, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.SMALL_PURPLE_DRIPLEAF, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_VINE, ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(VibraniumBlocks.HEART_SHAPED_HERB, ChunkSectionLayer.CUTOUT);

		//vine color fix
		// On force le bloc à rester "naturel" (blanc pur = pas de changement de couleur)
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0xFFFFFF, VibraniumBlocks.PURPLE_VINE);

	}


}