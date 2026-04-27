package io.github.leiriad.vibranium.neoforge.client;

import io.github.leiriad.vibranium.VibraniumMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = VibraniumMod.MOD_ID, value = Dist.CLIENT)
public class VibraniumModNeoForgeClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }


}


