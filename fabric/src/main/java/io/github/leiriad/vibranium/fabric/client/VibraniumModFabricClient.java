package io.github.leiriad.vibranium.fabric.client;

import io.github.leiriad.vibranium.client.VibraniumModClient;
import io.github.leiriad.vibranium.client.render.FluidTankRenderer;
import io.github.leiriad.vibranium.client.render.OreHighlightRenderer;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public final class VibraniumModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        VibraniumModClient.init();
        // Register the Block Entity Renderer for Fabric
        BlockEntityRenderers.register(VibraniumEntities.FLUID_TANK_ENTITY.get(), FluidTankRenderer::new);

        //Ore Highlight Event
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            Minecraft mc = Minecraft.getInstance();

            if (context.matrices() != null && context.consumers() != null && mc.gameRenderer.getMainCamera() != null) {
                OreHighlightRenderer.render(
                        context.matrices(),
                        context.consumers().getBuffer(RenderTypes.lines()),
                        mc.gameRenderer.getMainCamera().position()
                );
            }
        });
    }
}
