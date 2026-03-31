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
	}
}