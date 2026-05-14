package io.github.leiriad.vibranium.fabric.datagen;


import io.github.leiriad.vibranium.block.HeartShapedHerbFlower;
import io.github.leiriad.vibranium.init.VibraniumItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class VibraniumLootTableProvider extends FabricBlockLootTableProvider {
///JSON Loot table files automation
    CompletableFuture<HolderLookup.Provider> lookUp;

    public VibraniumLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
        lookUp = registryLookup;
    }

    @Override
    public void generate() {
        System.out.println("DEBUG: Lancement de la génération des tables de loot...");

        // Modifie temporairement une de tes méthodes pour voir ce qu'il se passe
        Block ore = VibraniumBlocks.VIBRANIUM_ORE.get();
        System.out.println("DEBUG: Vibranium Ore récupéré : " + ore);
        if (ore != null) {
            System.out.println("DEBUG: ID du bloc dans le registre : " + BuiltInRegistries.BLOCK.getKey(ore));
        }
        //Get enchantments
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        //Blocks
        createVibraniumOreLoot();
        createBlackClayLoot();
        createVibraniumDirtBlocksLoot();
        createBlackGravelLoot(enchantmentLookup);//BlackGRavel behaves differently with Fortune

        //Vegetals
        createPurpleGrassLoot();
        createPurpleAzaleaLoot();
        createPurpleAzaleaLeavesLoot();
        createFloweringPurpleAzaleaLoot();
        createFloweringPurpleAzaleaLeavesLoot();
        createPurpleMossLoot(VibraniumBlocks.PURPLE_MOSS_BLOCK.get());
        createPurpleMossLoot(VibraniumBlocks.PURPLE_MOSS_CARPET.get());
        createPurpleCaveVinesLoot(VibraniumBlocks.PURPLE_CAVE_VINES.get());
        createPurpleCaveVinesLoot(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get());
        createPottedAzaleaLoot();
        createDripleavesLoot();
        createVineLoot();
        createHeartShapedHerbLoot();

        //Industrial blocks

        createIndustrialBlocksLoot(VibraniumBlocks.REACTOR_CORE.get());
    }

    private void createVibraniumOreLoot() {
        //Ore drops itself with silk touch, drops vibranium dust otherwise
        Block VIBRANIUM_ORE = VibraniumBlocks.VIBRANIUM_ORE.get();
        if(VIBRANIUM_ORE != null){
            this.add(VIBRANIUM_ORE, (block) ->
                    createSilkTouchDispatchTable(block,
                            this.applyExplosionDecay(block, LootItem.lootTableItem(VibraniumItems.VIBRANIUM_DUST.get())))
            );
        }
    }
    private void createBlackClayLoot() {
        // Black clay mimics vanilla clay
        Block BLACK_CLAY = VibraniumBlocks.BLACK_CLAY.get();
        if(BLACK_CLAY != null){
            this.add(BLACK_CLAY, (block) ->
                    createSilkTouchDispatchTable(block,
                            this.applyExplosionDecay(block,
                                    LootItem.lootTableItem(VibraniumItems.BLACK_CLAY_BALL.get())
                                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4)))))
            );
        }
    }
    private void createVibraniumDirtBlocksLoot() {
        // Dirt blocks give normal dirt without silk touch
        List.of(VibraniumBlocks.VIBRANIUM_DIRT.get(),VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(),VibraniumBlocks.VIBRANIUM_PATH.get(),VibraniumBlocks.VIBRANIUM_FARMLAND.get())
                .forEach(block ->
                {
                    if (block != null) {
                        this.add(block, (b) -> createSilkTouchDispatchTable(b, LootItem.lootTableItem(Items.DIRT)));
                    }
                }
        );
    }
    private void createBlackGravelLoot(HolderLookup.RegistryLookup<Enchantment> enchantmentLookup) {
        // Black gravel mimics vanilla gravel
        Block BLACK_GRAVEL = VibraniumBlocks.BLACK_GRAVEL.get();
        if(BLACK_GRAVEL != null){
            this.add(BLACK_GRAVEL, (block) ->
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
    private void createPurpleGrassLoot() {
        List.of(VibraniumBlocks.PURPLE_SHORT_GRASS.get(),VibraniumBlocks.PURPLE_TALL_GRASS.get())
                .forEach(block ->
        {
            if (block != null) {
                this.add(block,(b)->
                        this.createShearsDispatchTable(b,
                                this.applyExplosionDecay(b,
                                        LootItem.lootTableItem(Items.WHEAT_SEEDS)
                                                .when(LootItemRandomChanceCondition.randomChance(0.125f))
                                )
                        )
                );
            }
        });

    }

    private void createPurpleAzaleaLoot(){
        Block PURPLE_AZALEA = VibraniumBlocks.PURPLE_AZALEA.get();
        if(PURPLE_AZALEA != null){
            this.add(PURPLE_AZALEA, (block) ->
                    createSilkTouchDispatchTable(block, LootItem.lootTableItem(Blocks.AZALEA)));
        }
    }
    private void createFloweringPurpleAzaleaLoot(){
        Block FLOWERING_PURPLE_AZALEA = VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get();
        if(FLOWERING_PURPLE_AZALEA != null){
            this.add(FLOWERING_PURPLE_AZALEA, (block) ->
                    createSilkTouchDispatchTable(block, LootItem.lootTableItem(Blocks.FLOWERING_AZALEA)));
        }
    }
    private void createPurpleAzaleaLeavesLoot() {
        List.of(VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(),VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(),VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get())
                .forEach(block ->
        {
            if(block != null){
                this.add(block, (b) ->
                        this.createLeavesDrops(
                                b,
                                VibraniumBlocks.PURPLE_AZALEA.get(),
                                0.05F, 0.0625F, 0.083333336F, 0.1F
                        )
                );
            }
        });
    }
    private void createFloweringPurpleAzaleaLeavesLoot() {
        List.of(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(),VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(),VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get())
                .forEach(block ->
        {
            if(block != null){
                this.add(block, (b) ->
                        this.createLeavesDrops(
                                b,
                                VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(),
                                0.05F, 0.0625F, 0.083333336F, 0.1F
                        )
                );
            }
        });
    }
    private void createPurpleMossLoot(Block mossblock) {
        if(mossblock != null){
            this.add(mossblock, (block) ->
                    this.createSingleItemTable(mossblock));
        }
    }
    private void createPurpleCaveVinesLoot(Block vinesBlock) {
        if (vinesBlock != null) {
            this.add(vinesBlock, (block) ->
                    LootTable.lootTable().withPool(
                            LootPool.lootPool()
                                    // On ne donne l'item BLUE_GLOW_BERRIES que si l'état BERRIES est true
                                    .add(LootItem.lootTableItem(VibraniumBlocks.BLUE_GLOW_BERRIES.get()))
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(CaveVines.BERRIES, true)))
                    )
            );
        }
    }
    private void createPottedAzaleaLoot(){
        List.of(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(),VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get())
                .forEach(block -> {
                    if(block instanceof FlowerPotBlock pot){
                        this.add(block,(b) -> createPotFlowerItemTable(pot.getPotted()));
                    }
                });
    }
    private void createDripleavesLoot(){
        List.of(VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(),VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get())
                .forEach(block ->
                {
                    if(block != null ){
                        this.add(block, (b) ->
                                this.createSingleItemTable(VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get()));

        }});
        Block SMALL_PURPLE_DRIPLEAF = VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get();
        if (SMALL_PURPLE_DRIPLEAF != null){
            this.add(SMALL_PURPLE_DRIPLEAF, (block) ->
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(block)
                                            .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                                    .of(this.registries.lookupOrThrow(Registries.ITEM), Items.SHEARS)))
                                    )
                            ));
        }
    }
    private void createVineLoot(){
        Block PURPLE_VINE = VibraniumBlocks.PURPLE_VINE.get();
        if (PURPLE_VINE != null){
            this.add(PURPLE_VINE, this::createShearsOnlyDrop);
        }
    }
    private void createHeartShapedHerbLoot(){
        Block flower = VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get();
        if (flower != null) {
            this.add(flower, (block) ->
                    // If silktouch, drop flower
                    createSilkTouchDispatchTable(block,
                            //Else if mature -> fruit or plant is broken
                            this.applyExplosionDecay(block,
                                    LootItem.lootTableItem(VibraniumItems.HEART_SHAPED_HERB.get())
                                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                    .setProperties(StatePropertiesPredicate.Builder.properties()
                                                            .hasProperty(HeartShapedHerbFlower.AGE, 2)))
                            )
                    )
            );
        }
    }
    private void createIndustrialBlocksLoot( Block industrialBlock) {
        if(industrialBlock != null){
            this.add(industrialBlock, (block) ->
                    this.createSingleItemTable(industrialBlock));
        }
    }
}