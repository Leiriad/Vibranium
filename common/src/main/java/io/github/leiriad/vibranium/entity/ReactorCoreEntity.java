package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.init.VibraniumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ReactorCoreEntity extends BlockEntity {
    //PROPERTIES
    private int temperature = 20; // Ambiante
    private int energyStored = 0;
    private final int MAX_ENERGY = 100000;
    public final SimpleContainer inventory = new SimpleContainer(2);
    private long waterAmount=0;
    private long hotWaterAmount=0;

    public ReactorCoreEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // CONSTRUCTOR
    public ReactorCoreEntity(BlockPos pos, BlockState state) {
        this(VibraniumEntities.REACTOR_CORE_ENTITY.get(), pos, state);
    }

    //METHODS
    // Game saving
    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        valueOutput.putInt("energy", energyStored);
        valueOutput.putInt("temperature", temperature);
        valueOutput.putLong("water", waterAmount);
        valueOutput.putLong("hot_water", hotWaterAmount);
        valueOutput.store("Inventory", ItemStack.OPTIONAL_CODEC.listOf(), this.inventory.getItems());
        super.saveAdditional(valueOutput);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.energyStored = valueInput.getInt("energy").orElse(0);
        this.temperature = valueInput.getInt("temperature").orElse(this.temperature);
        this.waterAmount = valueInput.getLong("water").orElse(0l);
        this.hotWaterAmount = valueInput.getLong("hot_water").orElse(0l);
        valueInput.read("Inventory", ItemStack.OPTIONAL_CODEC.listOf()).ifPresent(items -> {
            this.inventory.clearContent();
            for (int i = 0; i < items.size() && i < this.inventory.getContainerSize(); i++) {
                this.inventory.setItem(i, items.get(i));
            }
        });
    }

    //Process
    public static void tick(Level level, BlockPos pos, BlockState state, ReactorCoreEntity blockEntity) {
        if (level.isClientSide()) return;

        if (blockEntity.canProcess()) {
            blockEntity.process();
        } else {
            blockEntity.coolDown();
        }
    }

    private void coolDown() {
    }

    private boolean canProcess() {
        return inventory.getItem(0).is(VibraniumItems.VIBRANIUM_DUST.get()) && waterAmount >= 100;
    }

    private void process() {
        // Logique de transformation ici
        this.temperature += 5;
        this.energyStored += calculateEnergyOutput();
        // Consommation et production...
    }

    private int calculateEnergyOutput() {
        return 0;
    }
}
