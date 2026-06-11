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
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ReactorControlPanelMenu extends AbstractContainerMenu {

    //PROPERTIES
    private final ReactorCoreEntity reactor;
    private final ContainerData data;

    //CONSTRUCTORS
    //Client
    public ReactorControlPanelMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (ReactorCoreEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    //Server
    public ReactorControlPanelMenu(int id, Inventory inv, ReactorCoreEntity reactor) {
        super(VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get(), id);
        this.reactor = reactor;
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                if (reactor == null) return 0;
                return switch (index) {
                    case 0 -> reactor.getEnergy();
                    case 1 -> reactor.getTemperature();
                    case 2 -> reactor.getVibraniumAmount();
                    case 3 -> (int) (reactor.getWaterAmount() & 0xFFFFFFFFL);
                    case 4 -> (int) (reactor.getWaterAmount() >> 32);
                    case 5 -> (int) (reactor.getHotWaterAmount() & 0xFFFFFFFFL);
                    case 6 -> (int) (reactor.getHotWaterAmount() >> 32);
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) { }
            @Override
            public int getCount() { return 7; }
        };
        this.addDataSlots(this.data);
    }

    //METHODS
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    // Screen getters
    public int getEnergy() { System.out.println("Energie : " + this.data.get(0)); return this.data.get(0); }
    public int getHeat() { System.out.println("Chaleur : " + this.data.get(1)); return this.data.get(1); }
    public int getVibranium() {System.out.println("Vibranium : " + this.data.get(2)); return this.data.get(2); }

    public long getWater() {
        long low = this.data.get(3) & 0xFFFFFFFFL;// Long values are split on two memory spaces
        long high = (long) this.data.get(4) << 32;
        System.out.println("Eau : " + high +"|"+low);
        return high | low;
    }

    public long getHotWater() {
        long low = this.data.get(5) & 0xFFFFFFFFL;
        long high = (long) this.data.get(6) << 32;
        System.out.println("Eau chaude : " + high +"|"+low);
        return high | low;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.reactor == null || this.reactor.isRemoved()) return false;
        return player.distanceToSqr(reactor.getBlockPos().getCenter()) < 100.0;
    }
}
