package io.github.leiriad.vibranium.fabric.client;

import io.github.leiriad.vibranium.client.VibraniumModClient;
import io.github.leiriad.vibranium.client.render.FluidTankRenderer;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.MapItemColor;

public final class VibraniumModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        VibraniumModClient.init();
        // Register the Block Entity Renderer for Fabric
        BlockEntityRenderers.register(VibraniumEntities.FLUID_TANK_ENTITY.get(), FluidTankRenderer::new);
    }
}
