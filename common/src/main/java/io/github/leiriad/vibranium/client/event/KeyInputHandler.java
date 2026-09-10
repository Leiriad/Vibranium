package io.github.leiriad.vibranium.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.network.AbilityPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {

    // Translation keys for the Controls menu
    public static final String ITEM_ABILITY = "key." + VibraniumMod.MOD_ID + ".use_ability";

    public static KeyMapping itemAbilityKey;

    public static void register() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "general")
        );
        // Register key with default setting set to GLFW_KEY_V (Key 'V')
        itemAbilityKey = new KeyMapping(
                ITEM_ABILITY,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                category
        );

        KeyMappingRegistry.register(itemAbilityKey);

        // Listen to client ticks to send a network packet when pressed
        ClientTickEvent.CLIENT_POST.register(client -> {
            while (itemAbilityKey.consumeClick()) {
                NetworkManager.sendToServer(new AbilityPayload());
            }
        });
    }
}