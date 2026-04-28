package io.github.leiriad.vibranium.fabric.client;

import io.github.leiriad.vibranium.client.VibraniumModClient;
import net.fabricmc.api.ClientModInitializer;

public final class VibraniumModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VibraniumModClient.init();
    }
}
