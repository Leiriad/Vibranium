package vibranium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import vibranium.init.VibraniumBlocks;

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
		BlockRenderLayerMap.putBlock(VibraniumBlocks.PURPLE_TALL_GRASS, ChunkSectionLayer.CUTOUT);
	}
}