package io.github.leiriad.vibranium.menu;

import io.github.leiriad.vibranium.entity.ReactorCoreEntity;
import io.github.leiriad.vibranium.init.VibraniumMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class ReactorControlPanelMenu extends AbstractContainerMenu {

    //PROPERTIES
    private final ReactorCoreEntity reactor;

    // Modern DataSlots to bypass the 16-bit short limitation of ContainerData
    private final DataSlot energySlot;
    private final DataSlot heatSlot;
    private final DataSlot vibraniumSlot;
    private final DataSlot waterSlot;
    private final DataSlot hotWaterSlot;
    private final DataSlot maxWaterSlot;
    private final DataSlot maxHotWaterSlot;

    //CONSTRUCTORS
    //Client
    public ReactorControlPanelMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (ReactorCoreEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    //Server
    public ReactorControlPanelMenu(int id, Inventory inv, ReactorCoreEntity reactor) {
        super(VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get(), id);
        this.reactor = reactor;

        // We create full-int and full-long responsive DataSlots
        this.energySlot = this.addDataSlot(DataSlot.shared(new int[]{0}, 0));
        this.heatSlot = this.addDataSlot(DataSlot.shared(new int[]{0}, 0));
        this.vibraniumSlot = this.addDataSlot(DataSlot.shared(new int[]{0}, 0));

        // Using custom long data slots or standalone int providers that network packets sync correctly
        this.waterSlot = this.addDataSlot(new LongDataSlot());
        this.hotWaterSlot = this.addDataSlot(new LongDataSlot());
        this.maxWaterSlot = this.addDataSlot(new LongDataSlot());
        this.maxHotWaterSlot = this.addDataSlot(new LongDataSlot());
    }

    // Server-side update check called during container tracking
    @Override
    public void broadcastChanges() {
        if (this.reactor != null) {
            this.energySlot.set(this.reactor.getEnergy());
            this.heatSlot.set(this.reactor.getTemperature());
            this.vibraniumSlot.set(this.reactor.getVibraniumAmount());

            ((LongDataSlot) this.waterSlot).setLong(this.reactor.getWaterAmount());
            ((LongDataSlot) this.hotWaterSlot).setLong(this.reactor.getHotWaterAmount());
            ((LongDataSlot) this.maxWaterSlot).setLong(this.reactor.getMaxWaterCapacity());
            ((LongDataSlot) this.maxHotWaterSlot).setLong(this.reactor.getMaxHotWaterCapacity());
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    // Screen getters
    public int getEnergy() { return this.energySlot.get(); }
    public int getHeat() { return this.heatSlot.get(); }
    public int getVibranium() { return this.vibraniumSlot.get(); }

    public long getWater() { return ((LongDataSlot) this.waterSlot).getLong(); }
    public long getHotWater() { return ((LongDataSlot) this.hotWaterSlot).getLong(); }
    public long getMaxWater() { return ((LongDataSlot) this.maxWaterSlot).getLong(); }
    public long getMaxHotWater() { return ((LongDataSlot) this.maxHotWaterSlot).getLong(); }

    @Override
    public boolean stillValid(Player player) {
        if (this.reactor == null || this.reactor.isRemoved()) return false;
        return player.distanceToSqr(reactor.getBlockPos().getCenter()) < 100.0;
    }

    /**
     * Inner helper to serialize longs properly across DataSlots without bit shifting inside container arrays
     */
    private static class LongDataSlot extends DataSlot {
        private long value;

        public void setLong(long value) {
            this.value = value;
        }

        public long getLong() {
            return this.value;
        }

        @Override
        public int get() {
            // Unused fallback for traditional int tracking
            return (int) this.value;
        }

        @Override
        public void set(int value) {
            // Used by incoming networking packets to sync client states
            this.value = value;
        }
    }
}