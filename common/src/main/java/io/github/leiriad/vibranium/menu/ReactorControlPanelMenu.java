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

    // PROPERTIES
    private final ReactorCoreEntity reactor;

    // Splitting 32-bit integers into two 16-bit short slots to bypass vanilla networking limitations
    private final DataSlot energyLowerSlot;
    private final DataSlot energyUpperSlot;

    private final DataSlot heatLowerSlot;
    private final DataSlot heatUpperSlot;

    private final DataSlot vibraniumLowerSlot;
    private final DataSlot vibraniumUpperSlot;

    // Splitting 64-bit longs into four 16-bit short slots for fluid storage sync
    private final DataSlot water1, water2, water3, water4;
    private final DataSlot hotWater1, hotWater2, hotWater3, hotWater4;
    private final DataSlot maxWater1, maxWater2, maxWater3, maxWater4;
    private final DataSlot maxHotWater1, maxHotWater2, maxHotWater3, maxHotWater4;

    // CONSTRUCTORS
    // Client side
    public ReactorControlPanelMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (ReactorCoreEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    // Server side
    public ReactorControlPanelMenu(int id, Inventory inv, ReactorCoreEntity reactor) {
        super(VibraniumMenus.REACTOR_CONTROL_PANEL_MENU.get(), id);
        this.reactor = reactor;

        // Registering paired short slots for integers
        this.energyLowerSlot = this.addDataSlot(DataSlot.standalone());
        this.energyUpperSlot = this.addDataSlot(DataSlot.standalone());

        this.heatLowerSlot = this.addDataSlot(DataSlot.standalone());
        this.heatUpperSlot = this.addDataSlot(DataSlot.standalone());

        this.vibraniumLowerSlot = this.addDataSlot(DataSlot.standalone());
        this.vibraniumUpperSlot = this.addDataSlot(DataSlot.standalone());

        // Registering quadrupled short slots for longs
        this.water1 = this.addDataSlot(DataSlot.standalone());
        this.water2 = this.addDataSlot(DataSlot.standalone());
        this.water3 = this.addDataSlot(DataSlot.standalone());
        this.water4 = this.addDataSlot(DataSlot.standalone());

        this.hotWater1 = this.addDataSlot(DataSlot.standalone());
        this.hotWater2 = this.addDataSlot(DataSlot.standalone());
        this.hotWater3 = this.addDataSlot(DataSlot.standalone());
        this.hotWater4 = this.addDataSlot(DataSlot.standalone());

        this.maxWater1 = this.addDataSlot(DataSlot.standalone());
        this.maxWater2 = this.addDataSlot(DataSlot.standalone());
        this.maxWater3 = this.addDataSlot(DataSlot.standalone());
        this.maxWater4 = this.addDataSlot(DataSlot.standalone());

        this.maxHotWater1 = this.addDataSlot(DataSlot.standalone());
        this.maxHotWater2 = this.addDataSlot(DataSlot.standalone());
        this.maxHotWater3 = this.addDataSlot(DataSlot.standalone());
        this.maxHotWater4 = this.addDataSlot(DataSlot.standalone());
    }

    // METHODS
    @Override
    public void broadcastChanges() {
        if (this.reactor != null) {
            // Split integers into two 16-bit chunks
            int energy = this.reactor.getEnergy();
            this.energyLowerSlot.set(energy & 0xFFFF);
            this.energyUpperSlot.set((energy >> 16) & 0xFFFF);

            int heat = this.reactor.getTemperature();
            this.heatLowerSlot.set(heat & 0xFFFF);
            this.heatUpperSlot.set((heat >> 16) & 0xFFFF);

            int vibranium = this.reactor.getVibraniumAmount();
            this.vibraniumLowerSlot.set(vibranium & 0xFFFF);
            this.vibraniumUpperSlot.set((vibranium >> 16) & 0xFFFF);

            // Split longs into four 16-bit chunks
            long water = this.reactor.getWaterAmount();
            this.water1.set((int) (water & 0xFFFF));
            this.water2.set((int) ((water >> 16) & 0xFFFF));
            this.water3.set((int) ((water >> 32) & 0xFFFF));
            this.water4.set((int) ((water >> 48) & 0xFFFF));

            long hotWater = this.reactor.getHotWaterAmount();
            this.hotWater1.set((int) (hotWater & 0xFFFF));
            this.hotWater2.set((int) ((hotWater >> 16) & 0xFFFF));
            this.hotWater3.set((int) ((hotWater >> 32) & 0xFFFF));
            this.hotWater4.set((int) ((hotWater >> 48) & 0xFFFF));

            long maxWater = this.reactor.getMaxWaterCapacity();
            this.maxWater1.set((int) (maxWater & 0xFFFF));
            this.maxWater2.set((int) ((maxWater >> 16) & 0xFFFF));
            this.maxWater3.set((int) ((maxWater >> 32) & 0xFFFF));
            this.maxWater4.set((int) ((maxWater >> 48) & 0xFFFF));

            long maxHotWater = this.reactor.getMaxHotWaterCapacity();
            this.maxHotWater1.set((int) (maxHotWater & 0xFFFF));
            this.maxHotWater2.set((int) ((maxHotWater >> 16) & 0xFFFF));
            this.maxHotWater3.set((int) ((maxHotWater >> 32) & 0xFFFF));
            this.maxHotWater4.set((int) ((maxHotWater >> 48) & 0xFFFF));
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    // Screen getters recombining 16-bit data pieces into complete types
    public int getEnergy() {
        return (this.energyLowerSlot.get() & 0xFFFF) | ((this.energyUpperSlot.get() & 0xFFFF) << 16);
    }

    public int getHeat() {
        return (this.heatLowerSlot.get() & 0xFFFF) | ((this.heatUpperSlot.get() & 0xFFFF) << 16);
    }

    public int getVibranium() {
        return (this.vibraniumLowerSlot.get() & 0xFFFF) | ((this.vibraniumUpperSlot.get() & 0xFFFF) << 16);
    }

    public long getWater() {
        return (this.water1.get() & 0xFFFFL) |
                ((this.water2.get() & 0xFFFFL) << 16) |
                ((this.water3.get() & 0xFFFFL) << 32) |
                ((this.water4.get() & 0xFFFFL) << 48);
    }

    public long getHotWater() {
        return (this.hotWater1.get() & 0xFFFFL) |
                ((this.hotWater2.get() & 0xFFFFL) << 16) |
                ((this.hotWater3.get() & 0xFFFFL) << 32) |
                ((this.hotWater4.get() & 0xFFFFL) << 48);
    }

    public long getMaxWater() {
        return (this.maxWater1.get() & 0xFFFFL) |
                ((this.maxWater2.get() & 0xFFFFL) << 16) |
                ((this.maxWater3.get() & 0xFFFFL) << 32) |
                ((this.maxWater4.get() & 0xFFFFL) << 48);
    }

    public long getMaxHotWater() {
        return (this.maxHotWater1.get() & 0xFFFFL) |
                ((this.maxHotWater2.get() & 0xFFFFL) << 16) |
                ((this.maxHotWater3.get() & 0xFFFFL) << 32) |
                ((this.maxHotWater4.get() & 0xFFFFL) << 48);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.reactor == null || this.reactor.isRemoved()) return false;
        return player.distanceToSqr(reactor.getBlockPos().getCenter()) < 100.0;
    }
}