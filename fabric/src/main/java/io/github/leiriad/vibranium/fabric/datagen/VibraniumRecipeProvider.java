package io.github.leiriad.vibranium.fabric.datagen;
import io.github.leiriad.vibranium.init.VibraniumItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class VibraniumRecipeProvider extends FabricRecipeProvider {

    public VibraniumRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                // Main recipe configuration for autonomous furnace smelting
                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(VibraniumItems.VIBRANIUM_DUST.get()),    // Input item
                                RecipeCategory.MISC,                                   // Creative tab category
                                VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get(),         // Output item
                                0.7F,                                                  // Experience rewarded
                                200                                                    // Cooking time in ticks (10s)
                        )
                        // Unlock condition trigger
                        .unlockedBy(getHasName(VibraniumItems.VIBRANIUM_DUST.get()), has(VibraniumItems.VIBRANIUM_DUST.get()))
                        // Save the file using the provided exporter
                        .save(exporter, "vibranium_dust_to_depleted");
            }
        };
    }

    @Override
    public String getName() {
        return "Vibranium Recipes";
    }
}