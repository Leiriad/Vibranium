package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ReinforcedVibraniumGlassEntity extends BlockEntity {
    public ReinforcedVibraniumGlassEntity(BlockPos pos, BlockState state) {

        super(VibraniumEntities.REINFORCED_VIBRANIUM_GLASS_ENTITY.get(), pos, state);
    }

    // --- NETWORK SYNCHRONIZATION FOR CLIENT CABLES ---

    /**
     * Sends the block entity data to the client when the chunk loads.
     */
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * Generates the NBT data package sent to the client via getUpdatePacket.
     */
    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
}
