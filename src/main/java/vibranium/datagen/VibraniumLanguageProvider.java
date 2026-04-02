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
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_SOIL, "Terre de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, "Herbe vibranisée");
                break;
            case "es_es":
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE, "Mineral de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_SOIL, "Tierra de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, "Hierba vibranizada");
                break;
            default:
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_ORE, "Vibranium ore");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_SOIL, "Vibranium soil");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK, "Vibranized grass");
        }

    }
}

