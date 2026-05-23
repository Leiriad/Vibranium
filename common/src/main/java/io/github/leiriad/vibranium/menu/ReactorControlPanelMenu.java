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
    private final DataSlot energySlot = DataSlot.standalone();
    private final DataSlot heatSlot = DataSlot.standalone();
    private final DataSlot vibraniumSlot = DataSlot.standalone();

    //long values are split in two slots
    private final DataSlot waterLowSlot = DataSlot.standalone();
    private final DataSlot waterHighSlot = DataSlot.standalone();

    private final DataSlot hotWaterLowSlot = DataSlot.standalone();
    private final DataSlot hotWaterHighSlot = DataSlot.standalone();


    //CONSTRUCTORS
    //Client
    public ReactorControlPanelMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (ReactorCoreEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    //Server
    public ReactorControlPanelMenu(int id, Inventory inv, ReactorCoreEntity reactor) {
        super(VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get(), id);
        this.reactor = reactor;
        this.addDataSlot(energySlot);
        this.addDataSlot(heatSlot);
        this.addDataSlot(vibraniumSlot);
        this.addDataSlot(waterLowSlot);
        this.addDataSlot(waterHighSlot);
        this.addDataSlot(hotWaterLowSlot);
        this.addDataSlot(hotWaterHighSlot);
    }

    //METHODS
    ///Server values injection
    @Override
    public void broadcastChanges() {
        if (this.reactor != null) {
            int energy = this.reactor.getEnergy();
            int heat = this.reactor.getTemperature();
            int vibranium = this.reactor.getVibraniumAmount();
            long water = this.reactor.getWaterAmount();
            long hotWater = this.reactor.getHotWaterAmount();

            this.energySlot.set((int) energy);
            this.heatSlot.set((int) heat);
            this.vibraniumSlot.set((int) vibranium);
            this.waterLowSlot.set((int) (water & 0xFFFFFFFFL));
            this.waterHighSlot.set((int) (water >> 32));
            this.hotWaterLowSlot.set((int) (hotWater & 0xFFFFFFFFL));
            this.hotWaterHighSlot.set((int) (hotWater >> 32));
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    // Screen getters
    public int getEnergy() { return this.energySlot.get(); }
    public int getHeat() { return this.heatSlot.get(); }
    public int getVibranium() { return this.vibraniumSlot.get(); }
    public long getWater() {
        long low = this.waterLowSlot.get() & 0xFFFFFFFFL;
        long high = (long) this.waterHighSlot.get() << 32;
        return high | low;
    }

    public long getHotWater() {
        long low = this.hotWaterLowSlot.get() & 0xFFFFFFFFL;
        long high = (long) this.hotWaterHighSlot.get() << 32;
        return high | low;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.reactor == null || this.reactor.isRemoved()) return false;
        return player.distanceToSqr(reactor.getBlockPos().getCenter()) < 100.0;
    }
}
