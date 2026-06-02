package io.github.leiriad.vibranium.menu;

import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.entity.ReactorHatchEntity;
import io.github.leiriad.vibranium.init.VibraniumMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReactorHatchMenu extends AbstractContainerMenu {

    //PROPERTIES
    private final ReactorCoreEntity reactor;
    private final DataSlot fuelProgressSlot = DataSlot.standalone();

    //CONSTRUCTORS
    //Client
    public ReactorHatchMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getReactorFromHatch(playerInv, buf.readBlockPos()));
    }
    //Server
    public ReactorHatchMenu(int id, Inventory playerInv, ReactorCoreEntity reactor) {
        super(VibraniumMenus.REACTOR_HATCH_MENU.get(), id);
        this.reactor = reactor;
        this.addDataSlot(fuelProgressSlot);
        if(this.reactor !=null){
            this.addSlot(new Slot(this.reactor.inventory, 0,80,24));
            this.addSlot(new Slot(this.reactor.inventory, 1,80,56));
        }

        //Add player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    //METHODS
    private static ReactorCoreEntity getReactorFromHatch(Inventory playerInv, BlockPos blockPos) {
        if (playerInv.player.level().getBlockEntity(blockPos) instanceof ReactorHatchEntity hatch) {
            if (hatch.getConnectedCore(playerInv.player.level()) instanceof ReactorCoreEntity core) {
                return core;
            }
        }
        return null;
    }

    ///Server values injection
    @Override
    public void broadcastChanges() {
        if (this.reactor != null) {
            // On envoie le temps de carburant restant au client
            this.fuelProgressSlot.set(this.reactor.getVibraniumAmount());
        }
        super.broadcastChanges();
    }
    public int getFuelProgress() {
        return this.fuelProgressSlot.get();
    }
    @Override //Shift-click management
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
