package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.init.VibraniumEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ReinforcedVibraniumGlassEntity extends BlockEntity {
    private BlockPos corePos = null;
    public ReinforcedVibraniumGlassEntity(BlockPos pos, BlockState state) {
        super(VibraniumEntities.REINFORCED_VIBRANIUM_GLASS_ENTITY.get(), pos, state);
    }
    public void setCorePos(BlockPos corePos) {
        this.corePos = corePos;
        this.setChanged();
    }
    public BlockPos getCorePos() {
        return this.corePos;
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        if (this.corePos != null) {
            valueOutput.putInt("CoreX", this.corePos.getX());
            valueOutput.putInt("CoreY", this.corePos.getY());
            valueOutput.putInt("CoreZ", this.corePos.getZ());
        }
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        Optional<Integer> x = valueInput.getInt("CoreX");
        Optional<Integer> y = valueInput.getInt("CoreY");
        Optional<Integer> z = valueInput.getInt("CoreZ");
        if (x.isPresent() && y.isPresent() && z.isPresent()) {
            this.corePos = new BlockPos(x.get(), y.get(), z.get());
        } else {
            this.corePos = null;
        }
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
