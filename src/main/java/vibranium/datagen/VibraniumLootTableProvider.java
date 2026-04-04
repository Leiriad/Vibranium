package vibranium.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import vibranium.init.VibraniumBlocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;



public class VibraniumLootTableProvider extends FabricBlockLootTableProvider {

    CompletableFuture<HolderLookup.Provider> lookUp;

    public VibraniumLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
        lookUp = registryLookup;
    }

    @Override
    public void generate() {
        //Get enchantments
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        //Ore drops itself with silk touch, drops vibranium dust otherwise
        this.add(VibraniumBlocks.VIBRANIUM_ORE, (block) ->
                createSilkTouchDispatchTable(block,
                        this.applyExplosionDecay(block, LootItem.lootTableItem(VibraniumBlocks.VIBRANIUM_ORE)))
        );
        // Blackclay mimics vanilla clay
        this.add(VibraniumBlocks.BLACKCLAY, (block) ->
                createSilkTouchDispatchTable(block,
                        this.applyExplosionDecay(block,
                                LootItem.lootTableItem(Items.CLAY_BALL)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4)))))
        );

        // Dirt blocks give normal dirt withour silk touch
        List.of(
                VibraniumBlocks.VIBRANIUM_DIRT,
                VibraniumBlocks.VIBRANIUM_GRASS_BLOCK,
                VibraniumBlocks.VIBRANIUM_PATH,
                VibraniumBlocks.VIBRANIUM_FARMLAND
        ).forEach(block ->
                this.add(block, (b) -> createSilkTouchDispatchTable(b, LootItem.lootTableItem(Items.DIRT)))
        );

        // Blackgravel mimics vanilla gravel
        this.add(VibraniumBlocks.BLACKGRAVEL, (block) ->
                createSilkTouchDispatchTable(block,
                        this.applyExplosionCondition(block,
                                LootItem.lootTableItem(Items.FLINT)
                                        .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                                enchantmentLookup.getOrThrow(Enchantments.FORTUNE),0.1F, 0.14285715F, 0.25F, 1.0F))
                                        .otherwise(LootItem.lootTableItem(block))
                        )
                )
        );

    }
}