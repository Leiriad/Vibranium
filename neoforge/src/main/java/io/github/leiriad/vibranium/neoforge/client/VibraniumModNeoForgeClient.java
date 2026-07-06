package io.github.leiriad.vibranium.neoforge.client;


import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.client.VibraniumModClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
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


}


