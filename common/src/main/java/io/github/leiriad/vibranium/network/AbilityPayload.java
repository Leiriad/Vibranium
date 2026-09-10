package io.github.leiriad.vibranium.network;

import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AbilityPayload() implements CustomPacketPayload {

    public static final Type<AbilityPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "trigger_ability"));

    public static final StreamCodec<FriendlyByteBuf, AbilityPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {},
            buf -> new AbilityPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}