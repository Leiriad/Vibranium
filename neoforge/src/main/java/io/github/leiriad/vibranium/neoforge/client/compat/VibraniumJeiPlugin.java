package io.github.leiriad.vibranium.neoforge.client.compat;

import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

@JeiPlugin
public class VibraniumJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath("vibranium", "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var ingredientManager = registration.getIngredientManager();

        // Hide Items / Blocks from JEI
        if (VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get() != null) {
            ItemStack stemStack = new ItemStack(VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get());
            ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(stemStack));
        }
        if (VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get() != null) {
            ItemStack stemStack = new ItemStack(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get());
            ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(stemStack));
        }
        if (VibraniumBlocks.VIBRANIUM_FARMLAND.get() != null) {
            ItemStack stemStack = new ItemStack(VibraniumBlocks.VIBRANIUM_FARMLAND.get());
            ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(stemStack));
        }
        if (VibraniumBlocks.VIBRANIUM_PATH.get() != null) {
            ItemStack stemStack = new ItemStack(VibraniumBlocks.VIBRANIUM_PATH.get());
            ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(stemStack));
        }

        // 2. Hide Fluids using NeoForge's FluidStack
        if (VibraniumFluids.VANILLA_MILK_STILL.get() != null) {
            FluidStack milkStack = new FluidStack(VibraniumFluids.VANILLA_MILK_STILL.get(), 1000);
            ingredientManager.removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, List.of(milkStack));
        }

        if (VibraniumFluids.HOT_WATER_STILL.get() != null) {
            FluidStack hotWaterStack = new FluidStack(VibraniumFluids.HOT_WATER_STILL.get(), 1000);
            ingredientManager.removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, List.of(hotWaterStack));
        }
    }
}
