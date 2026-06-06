package io.github.leiriad.vibranium.fabric.client;

import io.github.leiriad.vibranium.client.VibraniumModClient;
import io.github.leiriad.vibranium.client.render.FluidTankRenderer;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class VibraniumModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        VibraniumModClient.init();
        // Register the Block Entity Renderer for Fabric
        BlockEntityRenderers.register(VibraniumEntities.FLUID_TANK_ENTITY.get(), FluidTankRenderer::new);
    }
}
