package io.github.leiriad.vibranium.menu;

import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.entity.ReactorHatchEntity;
import io.github.leiriad.vibranium.init.VibraniumMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class ReactorHatchMenu extends AbstractContainerMenu {

    //PROPERTIES
    private BlockPos hatchPos;
    private ReactorHatchEntity hatch = null;
    private final ContainerData data;

    //CONSTRUCTORS
    //Client
    public ReactorHatchMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, buf.readBlockPos());
    }
    //Server
    public ReactorHatchMenu(int id, Inventory playerInv, BlockPos pos) {
        super(VibraniumMenus.REACTOR_HATCH_MENU.get(), id);
        this.hatchPos = pos;

        this.hatch = getHatchAtPos(playerInv.player.level(), pos);

        this.data = this.hatch != null ? this.hatch.data : new SimpleContainerData(1);
        this.addDataSlots(this.data);
        if (this.hatch != null) {
            this.addSlot(new Slot(this.hatch.inventory, 0, 80, 24));
            this.addSlot(new Slot(this.hatch.inventory, 1, 80, 56));
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
    private static ReactorHatchEntity getHatchAtPos(net.minecraft.world.level.Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ReactorHatchEntity hatch) {
            return hatch;
        }
        return null;
    }

    ///Server values injection
    public int getFuelProgress() {
        return this.data.get(0);
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
