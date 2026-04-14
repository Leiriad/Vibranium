package vibranium.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import vibranium.init.VibraniumBlocks;

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
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE, "Minerai de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DIRT, "Terre vibranisée");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, "Herbe vibranisée");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_PATH, "Chemin de terre vibranisée");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_FARMLAND, "Terre vibranisée labourée");
                translationBuilder.add(VibraniumBlocks.BLACKGRAVEL, "Gravier noir");
                translationBuilder.add(VibraniumBlocks.BLACKCLAY, "Argile noire");
                translationBuilder.add(VibraniumBlocks.PURPLE_SHORT_GRASS, "Herbe courte pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_TALL_GRASS, "Herbe haute pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA, "Azalée pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET, "Feuilles d'azalée pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE, "Feuilles d'azalée pourpre indigo");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN, "Feuilles d'azalée pourpre cyan");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA, "Azalée pourpre fleuri");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET, "Feuilles d'azalée pourpre fleuri");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE, "Feuilles d'azalée pourpre fleuri indigo");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN, "Feuilles d'azalée pourpre fleuri cyan");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_BLOCK, "Block de mousse pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_CARPET, "Tapis de mousse pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES, "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT, "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH, "Azalée pourpre en pot");
                translationBuilder.add(VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH, "Azalée pourpre fleuri en pot");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF, "Grande foliogoutte pourpre");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM, "Grande foliogoutte pourpre");
                translationBuilder.add(VibraniumBlocks.SMALL_PURPLE_DRIPLEAF, "Petite foliogoutte pourpre");
                break;
            case "es_es":
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE, "Mineral de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DIRT, "Tierra vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, "Hierba vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_PATH, "Camino de tierra vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_FARMLAND, "Tierra vibranizada de cultivo");
                translationBuilder.add(VibraniumBlocks.BLACKGRAVEL, "Grava negra");
                translationBuilder.add(VibraniumBlocks.BLACKCLAY, "Arcilla negra");
                translationBuilder.add(VibraniumBlocks.PURPLE_SHORT_GRASS, "Hierba corta morada");
                translationBuilder.add(VibraniumBlocks.PURPLE_TALL_GRASS, "Hierba alta morada");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA, "Azalea morada");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET, "Hojas de azalea morada");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE, "Hojas de azalea morada indigo");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN, "Hojas de azalea morada cian");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA, "Azalea morada florecida");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET, "Hojas de azalea morada florecida");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE, "Hojas de azalea morada florecida indigo");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN, "Hojas de azalea morada florecida cian");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_BLOCK, "Bloque de musgo morado");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_CARPET, "Alfombra de musgo morado");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES, "Enredaderas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT, "Enredaderas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH, "Maceta con azalea morada");
                translationBuilder.add(VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH, "Maceta con azalea morada florecida");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF, "Plantaforma morada grande");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM, "Plantaforma morada grande");
                translationBuilder.add(VibraniumBlocks.SMALL_PURPLE_DRIPLEAF, "Plantaforma morada pequeña");
                break;
            default:
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE, "Vibranium Ore");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DIRT, "Vibranized Dirt");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, "Vibranized Grass ");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_PATH, "Vibranized Dirt Path");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_FARMLAND, "Vibranised Farmland");
                translationBuilder.add(VibraniumBlocks.BLACKGRAVEL, "Black Gravel");
                translationBuilder.add(VibraniumBlocks.BLACKCLAY, "Black Clay");
                translationBuilder.add(VibraniumBlocks.PURPLE_SHORT_GRASS, "Purple Short Grass");
                translationBuilder.add(VibraniumBlocks.PURPLE_TALL_GRASS, "Purple tall Grass");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA, "Purple Azalea");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET, "Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE, "Indigo Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN, "Cyan Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA, "Flowering Purple Azalea");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET, "Flowering Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE, "Flowering Indigo Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN, "Flowering Cyan Purple Azalea Leaves");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_BLOCK, "Purple Moss Block");
                translationBuilder.add(VibraniumBlocks.PURPLE_MOSS_CARPET, "Purple Moss Carpet");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES, "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT, "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH, "Potted Purple Azalea");
                translationBuilder.add(VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH, "Potted Flowering Purple Azalea");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF, "Big Purple Dripleaf");
                translationBuilder.add(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM, "Big Purple Dripleaf");
                translationBuilder.add(VibraniumBlocks.SMALL_PURPLE_DRIPLEAF, "Small Purple Dripleaf");
        }

    }
}

