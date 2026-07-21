package io.github.leiriad.vibranium.fabric.client.compat;

// English comments as requested
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import me.shedaniel.rei.api.common.util.EntryStacks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.fabric.ingredients.fluids.JeiFluidIngredient;
import mezz.jei.api.registration.IRecipeRegistration;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
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

        // Hide Fluids using the record constructor and Fabric FluidVariant
        if (VibraniumFluids.VANILLA_MILK_STILL.get() != null) {
            var milkVariant = FluidVariant.of(VibraniumFluids.VANILLA_MILK_STILL.get());
            var milkIngredient = new JeiFluidIngredient(milkVariant, 1000);

            ingredientManager.removeIngredientsAtRuntime(FabricTypes.FLUID_STACK, List.of(milkIngredient));
        }

        if (VibraniumFluids.HOT_WATER_STILL.get() != null) {
            var hotWaterVariant = FluidVariant.of(VibraniumFluids.HOT_WATER_STILL.get());
            var hotWaterIngredient = new JeiFluidIngredient(hotWaterVariant, 1000);

            ingredientManager.removeIngredientsAtRuntime(FabricTypes.FLUID_STACK, List.of(hotWaterIngredient));
        }
    }
}