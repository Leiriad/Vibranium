package vibranium.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import vibranium.init.VibraniumBlocks;

import java.util.concurrent.CompletableFuture;

public class VibraniumLootTableProvider extends FabricBlockLootTableProvider {

    public VibraniumLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {

        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // Make blocs drop themselves with silk touch only
        dropWhenSilkTouch(VibraniumBlocks.VIBRANIUM_ORE);
        dropWhenSilkTouch(VibraniumBlocks.VIBRANIUM_DIRT);
        dropWhenSilkTouch(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK);
        dropWhenSilkTouch(VibraniumBlocks.VIBRANIUM_PATH);

        //Make blocks drop loot otherwise
        //ORE drops vibranium_dust
        //All dirt variants drop dirt
        add(VibraniumBlocks.VIBRANIUM_DIRT, LootTable.lootTable().withPool(applyExplosionCondition(Items.DIRT, LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.DIRT)))));
        add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, LootTable.lootTable().withPool(applyExplosionCondition(Items.DIRT, LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.DIRT)))));
        add(VibraniumBlocks.VIBRANIUM_PATH, LootTable.lootTable().withPool(applyExplosionCondition(Items.DIRT, LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.DIRT)))));

    }
}