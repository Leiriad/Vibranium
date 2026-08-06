package io.github.leiriad.vibranium.neoforge.client;


import com.mojang.blaze3d.vertex.PoseStack;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.client.VibraniumModClient;
import io.github.leiriad.vibranium.client.render.OreHighlightRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = VibraniumMod.MOD_ID, value = Dist.CLIENT)
public class VibraniumModNeoForgeClient {
    // The static block runs as soon as NeoForge loads this class,
    // registering the config screen perfectly in the ModLoadingContext.
    static {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (container, parentScreen) -> NeoForgeVibraniumScreen.create(parentScreen)
        );
    }
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        VibraniumModClient.init();
    }
    @SubscribeEvent
    public static void onRenderAfterEntities(RenderLevelStageEvent.AfterEntities event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null && mc.gameRenderer.getMainCamera() != null) {
            PoseStack poseStack = event.getPoseStack();
            var bufferSource = mc.renderBuffers().bufferSource();

            OreHighlightRenderer.render(
                    poseStack,
                    bufferSource.getBuffer(RenderTypes.lines()),
                    mc.gameRenderer.getMainCamera().position()
            );
        }
    }

}


