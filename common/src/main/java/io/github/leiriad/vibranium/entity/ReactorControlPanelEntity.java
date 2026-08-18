package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.ReactorControlPanelBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.menu.ReactorControlPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ReactorControlPanelEntity extends BlockEntity implements MenuProvider {
    public ReactorControlPanelEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public ReactorControlPanelEntity(BlockPos pos, BlockState state) {
        super(VibraniumEntities.REACTOR_CONTROL_PANEL_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.vibranium.reactor_control_panel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ReactorCoreEntity reactor = this.findNearbyCore();

        if (reactor != null) {
            System.out.println("[Vibranium Reactor Panel]: Core found at " + reactor.getBlockPos());
            return new ReactorControlPanelMenu(id, inventory, reactor);
        }

        System.out.println("[Vibranium Reactor Panel] Error: No reactor found.");
        return null;
    }

    @Nullable
    public ReactorCoreEntity findNearbyCore() {
        if (this.level == null) return null;

        int radius = 5;
        BlockPos coinMin = this.worldPosition.offset(-radius, -radius, -radius);
        BlockPos coinMax = this.worldPosition.offset(radius, radius, radius);

        for (BlockPos deplacement : BlockPos.betweenClosed(coinMin, coinMax)) {
            BlockEntity be = this.level.getBlockEntity(deplacement);
            if (be instanceof ReactorCoreEntity reactor) {
                return reactor; // On a trouvé le coeur !
            }
        }
        return null;
    }

    // --- NETWORK SYNCHRONIZATION ---

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
}
