package io.github.leiriad.vibranium.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

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
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE.get(), "Minerai de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DIRT.get(), "Terre vibranisée");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Herbe vibranisée");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_PATH.get(), "Chemin de terre vibranisée");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Terre vibranisée labourée");
                translationBuilder.add(VibraniumBlocks.BLACKGRAVEL.get(), "Gravier noir");
                translationBuilder.add(VibraniumBlocks.BLACKCLAY.get(), "Argile noire");
                translationBuilder.add(VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Herbe courte pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Herbe haute pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA.get(), "Azalée pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Feuilles d'azalée pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Feuilles d'azalée pourpre indigo");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Feuilles d'azalée pourpre cyan");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Azalée pourpre fleuri");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Feuilles d'azalée pourpre fleuri");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Feuilles d'azalée pourpre fleuri indigo");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Feuilles d'azalée pourpre fleuri cyan");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Block de mousse pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Tapis de mousse pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Azalée pourpre en pot");
                translationBuilder.add(VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Azalée pourpre fleuri en pot");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Grande foliogoutte pourpre");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Tige de grande foliogoutte pourpre");
                translationBuilder.add(VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Petite foliogoutte pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_VINE.get(), "Liane pourpre");
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB.get(), "Pousse d'herbe cœur");
                break;
            case "es_es":
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE.get(), "Mineral de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DIRT.get(), "Tierra vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Hierba vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_PATH.get(), "Camino de tierra vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Tierra vibranizada de cultivo");
                translationBuilder.add(VibraniumBlocks.BLACKGRAVEL.get(), "Grava negra");
                translationBuilder.add(VibraniumBlocks.BLACKCLAY.get(), "Arcilla negra");
                translationBuilder.add(VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Hierba corta morada");
                translationBuilder.add(VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Hierba alta morada");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA.get(), "Azalea morada");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Hojas de azalea morada");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Hojas de azalea morada indigo");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Hojas de azalea morada cian");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Azalea morada florecida");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Hojas de azalea morada florecida");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Hojas de azalea morada florecida indigo");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Hojas de azalea morada florecida cian");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Bloque de musgo morado");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Alfombra de musgo morado");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Enredaderas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Enredaderas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Maceta con azalea morada");
                translationBuilder.add(VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Maceta con azalea morada florecida");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Plantaforma morada grande");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Tallo de plantaforma morada grande");
                translationBuilder.add(VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Plantaforma morada pequeña");
                translationBuilder.add(VibraniumBlocks.PURPLE_VINE.get(), "Enredadera morada");
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB.get(), "Hierba en forma de corazón");
                break;
            default:
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE.get(), "Vibranium Ore");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DIRT.get(), "Vibranized Dirt");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Vibranized Grass ");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_PATH.get(), "Vibranized Dirt Path");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Vibranised Farmland");
                translationBuilder.add(VibraniumBlocks.BLACKGRAVEL.get(), "Black Gravel");
                translationBuilder.add(VibraniumBlocks.BLACKCLAY.get(), "Black Clay");
                translationBuilder.add(VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Purple Short Grass");
                translationBuilder.add(VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Purple tall Grass");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA.get(), "Purple Azalea");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Indigo Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Cyan Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Flowering Purple Azalea");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Flowering Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Flowering Indigo Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Flowering Cyan Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Purple Moss Block");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Purple Moss Carpet");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Potted Purple Azalea");
                translationBuilder.add(VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Potted Flowering Purple Azalea");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Big Purple Dripleaf");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Big Purple Dripleaf Stem");
                translationBuilder.add(VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Small Purple Dripleaf");
                translationBuilder.add(VibraniumBlocks.PURPLE_VINE.get(), "Purple Vine");
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB.get(), "Heart-shaped Herb");
        }

    }
}

