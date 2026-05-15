package io.github.leiriad.vibranium.menu;

import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ReactorControlPanelMenu extends AbstractContainerMenu {
    private final ReactorCoreEntity reactor;
    private final ContainerData data;

    public ReactorControlPanelMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, (ReactorCoreEntity) inv.player.level().getBlockEntity(pos));
    }
    public ReactorControlPanelMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getReactorEntity(playerInv.player.level(), buf.readBlockPos()));
    }
    public ReactorControlPanelMenu(int id, Inventory inv, ReactorCoreEntity reactor) {
        super(VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get(), id);
        this.reactor = reactor;

        // Create link to variables through an int array
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> reactor.getTemperature();
                    case 1 -> (int) reactor.getEnergyStored();

                    //water longs must be divided: DataSlot limit is 32767
                    case 2 -> (int) (reactor.getWaterAmount() & 0xFFFF);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                //Client never changes reactor data directly
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        this.addDataSlots(data);
        // TO DO : Ajouter les slots de l'inventaire du joueur ici pour pouvoir fermer le menu
    }
    private static ReactorCoreEntity getReactorEntity(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ReactorCoreEntity core) {
            return core;
        }

        // Si on arrive ici, c'est que la position reçue est celle du panneau !
        // Il faut donc refaire le scan ici aussi, ou envoyer la bonne pos depuis le serveur.
        System.err.println("ERREUR : La BlockEntity à " + pos + " n'est pas le réacteur mais " + (be != null ? be.getClass().getSimpleName() : "null"));
        return null;
    }
    // Screen getter
    public int getTemperature() { return this.data.get(0); }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(reactor.getLevel(), reactor.getBlockPos()),
                player,
                VibraniumBlocks.REACTOR_CORE.get()
        );
    }

    public int getEnergy() {
        return 0;
    }
}
