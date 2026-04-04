package vibranium.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import vibranium.init.VibraniumBlocks;

import java.util.concurrent.CompletableFuture;

public class VibraniumLanguageProvider extends FabricLanguageProvider {
    private String languageCode = "";

    //Default en constructor
    public VibraniumLanguageProvider (FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup){
        this(dataOutput, registryLookup, "en_us");
    }

    //Constructor for other languages
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
                break;
            case "es_es":
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE, "Mineral de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DIRT, "Tierra vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, "Hierba vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_PATH, "Camino de tierra vibranizada");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_FARMLAND, "Tierra vibranizada de cultivo");
                translationBuilder.add(VibraniumBlocks.BLACKGRAVEL, "Grava negra");
                break;
            default:
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE, "Vibranium Ore");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DIRT, "Vibranized Dirt");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, "Vibranized Grass ");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_PATH, "Vibranized Dirt Path");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_FARMLAND, "Vibranised Farmland");
                translationBuilder.add(VibraniumBlocks.BLACKGRAVEL, "Black Gravel");
        }

    }
}

