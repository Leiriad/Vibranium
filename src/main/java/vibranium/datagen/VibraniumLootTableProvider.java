package vibranium.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
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

        //Blocks
        createVibraniumOreLoot();
        createBlackClayLoot();
        createVibraniumDirtBlocksLoot();
        createBlackGravelLoot(enchantmentLookup);//BlackGRavel behaves differently with Fortune

        //Vegetals
        createPurpleShortGrassLoot();
        createPurpleTallGrassLoot();
        createPurpleAzaleaLoot();
        createPurpleAzaleaLeavesLoot(enchantmentLookup);
        createFloweringPurpleAzaleaLoot();
        createFloweringPurpleAzaleaLeavesLoot(enchantmentLookup);
        createPurpleMossLoot(VibraniumBlocks.PURPLE_MOSS_BLOCK);
        createPurpleMossLoot(VibraniumBlocks.PURPLE_MOSS_CARPET);
        createPurpleCaveVinesLoot(VibraniumBlocks.PURPLE_CAVE_VINES);
        createPurpleCaveVinesLoot(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT);
        createPottedAzaleaLoot();
    }

    private void createVibraniumOreLoot() {
        //Ore drops itself with silk touch, drops vibranium dust otherwise
        this.add(VibraniumBlocks.VIBRANIUM_ORE, (block) ->
                createSilkTouchDispatchTable(block,
                        this.applyExplosionDecay(block, LootItem.lootTableItem(VibraniumBlocks.VIBRANIUM_ORE)))
        );
    }

    private void createBlackClayLoot() {
        // Blackclay mimics vanilla clay
        this.add(VibraniumBlocks.BLACKCLAY, (block) ->
                createSilkTouchDispatchTable(block,
                        this.applyExplosionDecay(block,
                                LootItem.lootTableItem(Items.CLAY_BALL)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4)))))
        );
    }

    private void createVibraniumDirtBlocksLoot() {
        // Dirt blocks give normal dirt without silk touch
        List.of(
                VibraniumBlocks.VIBRANIUM_DIRT,
                VibraniumBlocks.VIBRANIUM_GRASS_BLOCK,
                VibraniumBlocks.VIBRANIUM_PATH,
                VibraniumBlocks.VIBRANIUM_FARMLAND
        ).forEach(block ->
                this.add(block, (b) -> createSilkTouchDispatchTable(b, LootItem.lootTableItem(Items.DIRT)))
        );
    }

    private void createBlackGravelLoot(HolderLookup.RegistryLookup<Enchantment> enchantmentLookup) {
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

    private void createPurpleShortGrassLoot() {
        this.add(VibraniumBlocks.PURPLE_SHORT_GRASS,(block)->
                this.createShearsDispatchTable(block,
                        this.applyExplosionDecay(block,
                                LootItem.lootTableItem(Items.WHEAT_SEEDS)
                                        .when(LootItemRandomChanceCondition.randomChance(0.125f))
                        )
                )
        );
    }

    private void createPurpleTallGrassLoot() {
        this.add(VibraniumBlocks.PURPLE_TALL_GRASS,(block)->
                this.createShearsDispatchTable(block,
                        this.applyExplosionDecay(block,
                                LootItem.lootTableItem(Items.WHEAT_SEEDS)
                                        .when(LootItemRandomChanceCondition.randomChance(0.125f))
                        )
                )
        );
    }

    private void createPurpleAzaleaLoot(){
        this.add(VibraniumBlocks.PURPLE_AZALEA, (block) ->
                createSilkTouchDispatchTable(block, LootItem.lootTableItem(Blocks.AZALEA)));
    }
    private void createFloweringPurpleAzaleaLoot(){
        this.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA, (block) ->
                createSilkTouchDispatchTable(block, LootItem.lootTableItem(Blocks.FLOWERING_AZALEA)));
    }

    private void createPurpleAzaleaLeavesLoot(HolderLookup.RegistryLookup<Enchantment> enchantmentLookup) {
        List.of(
                VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET,
                VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE,
                VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN
        ).forEach(leafBlock ->
                this.add(leafBlock, (block) ->
                        this.createLeavesDrops(
                                block,
                                VibraniumBlocks.PURPLE_AZALEA,
                                0.05F, 0.0625F, 0.083333336F, 0.1F
                        )
                )
        );
    }
    private void createFloweringPurpleAzaleaLeavesLoot(HolderLookup.RegistryLookup<Enchantment> enchantmentLookup) {
        List.of(
                VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET,
                VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE,
                VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN
        ).forEach(leafBlock ->
                this.add(leafBlock, (block) ->
                        this.createLeavesDrops(
                                block,
                                VibraniumBlocks.FLOWERING_PURPLE_AZALEA,
                                0.05F, 0.0625F, 0.083333336F, 0.1F
                        )
                )
        );
    }
    private void createPurpleMossLoot(Block mossblock) {
        this.add(mossblock, (block) ->
                this.createSingleItemTable(mossblock));
    }
    private void createPurpleCaveVinesLoot(Block block) {
        this.add(block, (vines) ->
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(vines)) // Loots cave vine
                ).withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.GLOW_BERRIES)) // Loots droppable item (berries)
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(vines)
                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(CaveVines.BERRIES, true))) // only if the vine contains berries
                )
        );
    }
    private void createPottedAzaleaLoot(){
        this.add(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH,(block) -> createPotFlowerItemTable(((FlowerPotBlock) block).getPotted()));
        this.add(VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH,(block) -> createPotFlowerItemTable(((FlowerPotBlock) block).getPotted()));

    }

}