package io.github.leiriad.vibranium.network;

import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record OreHighlightPayload(List<BlockPos> orePositions, int durationTicks) implements CustomPacketPayload {
    public static final Type<OreHighlightPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "ore_highlight"));

    public static final StreamCodec<FriendlyByteBuf, OreHighlightPayload> CODEC = CustomPacketPayload.codec(
            OreHighlightPayload::write,
            OreHighlightPayload::new
    );

    public OreHighlightPayload(FriendlyByteBuf buf) {
        this(buf.readList(b -> b.readBlockPos()), buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(orePositions, (b, pos) -> b.writeBlockPos(pos));
        buf.writeInt(durationTicks);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}