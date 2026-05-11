package io.github.leiriad.vibranium.fabric.datagen;

import io.github.leiriad.vibranium.init.VibraniumItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class VibraniumLanguageProvider extends FabricLanguageProvider {
    ///JSON language file automation
    private String languageCode = "";

    ///Default en constructor
    public VibraniumLanguageProvider (FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup){
        this(dataOutput, registryLookup, "en_us");
    }

    ///Constructor for other languages
    public VibraniumLanguageProvider (FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup, String languageCode){
        super(dataOutput, languageCode, registryLookup);
        this.languageCode=languageCode;
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        switch (languageCode){
            case "fr_fr":
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_ORE.get(), "Minerai de vibranium");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_DIRT.get(), "Terre vibranisée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Herbe vibranisée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_PATH.get(), "Chemin de terre vibranisée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Terre vibranisée labourée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACKGRAVEL.get(), "Gravier noir");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACKCLAY.get(), "Argile noire");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Herbe courte pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Herbe haute pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA.get(), "Azalée pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Feuilles d'azalée pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Feuilles d'azalée pourpre indigo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Feuilles d'azalée pourpre cyan");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Azalée pourpre fleuri");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Feuilles d'azalée pourpre fleuri");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Feuilles d'azalée pourpre fleuri indigo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Feuilles d'azalée pourpre fleuri cyan");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Block de mousse pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Tapis de mousse pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Azalée pourpre en pot");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Azalée pourpre fleuri en pot");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Grande foliogoutte pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Tige de grande foliogoutte pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Petite foliogoutte pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_VINE.get(), "Liane pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.HEART_SHAPED_HERB.get(), "Pousse d'herbe cœur");

                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Baies lumineuses bleues");
                translationBuilder.add(VibraniumItems.VIBRANIUM_DUST.get(), "Poudre de vibranium");
                break;
            case "es_es":
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_ORE.get(), "Mineral de vibranio");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_DIRT.get(), "Tierra vibranizada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Hierba vibranizada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_PATH.get(), "Camino de tierra vibranizada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Tierra vibranizada de cultivo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACKGRAVEL.get(), "Grava negra");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACKCLAY.get(), "Arcilla negra");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Hierba corta morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Hierba alta morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA.get(), "Azalea morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Hojas de azalea morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Hojas de azalea morada indigo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Hojas de azalea morada cian");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Azalea morada florecida");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Hojas de azalea morada florecida");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Hojas de azalea morada florecida indigo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Hojas de azalea morada florecida cian");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Bloque de musgo morado");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Alfombra de musgo morado");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Maceta con azalea morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Maceta con azalea morada florecida");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Plantaforma morada grande");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Tallo de plantaforma morada grande");
                addBlockWithItem(translationBuilder,VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Plantaforma morada pequeña");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_VINE.get(), "Enredadera morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.HEART_SHAPED_HERB.get(), "Hierba en forma de corazón");

                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Lianas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Enredaderas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Baya luminosas azules");
                translationBuilder.add(VibraniumItems.VIBRANIUM_DUST.get(), "Polvo de vibranio");
                break;
            default:
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_ORE.get(), "Vibranium Ore");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_DIRT.get(), "Vibranized Dirt");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Vibranized Grass ");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_PATH.get(), "Vibranized Dirt Path");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Vibranised Farmland");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACKGRAVEL.get(), "Black Gravel");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACKCLAY.get(), "Black Clay");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Purple Short Grass");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Purple tall Grass");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA.get(), "Purple Azalea");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Indigo Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Cyan Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Flowering Purple Azalea");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Flowering Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Flowering Indigo Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Flowering Cyan Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Purple Moss Block");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Purple Moss Carpet");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Potted Purple Azalea");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Potted Flowering Purple Azalea");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Big Purple Dripleaf");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Big Purple Dripleaf Stem");
                addBlockWithItem(translationBuilder,VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Small Purple Dripleaf");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_VINE.get(), "Purple Vine");
                addBlockWithItem(translationBuilder,VibraniumBlocks.HEART_SHAPED_HERB.get(), "Heart-shaped Herb");

                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Blue Glow Berries");
                translationBuilder.add(VibraniumItems.VIBRANIUM_DUST.get(), "Vibranium Dust");
        }

    }
    ///To provide double translations for block items
    private void addBlockWithItem(TranslationBuilder builder, net.minecraft.world.level.block.Block block, String name) {
        if (block == null) return;
        builder.add(block, name);

        Item item = block.asItem();
        if (item != net.minecraft.world.item.Items.AIR) {
            builder.add(item, name);
        }
    }
}

