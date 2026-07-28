package io.github.leiriad.vibranium.init;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.function.Supplier;

public class VibraniumBrewingRecipes {
    @ExpectPlatform
    public static void registerPotionToItemRecipe(ItemStack inputPotion, ItemStack ingredient, ItemStack output) {
        throw new AssertionError("Architectury ExpectPlatform stub");
    }

    public static void register() {
        registerPotionToItemRecipe(
                // Wrapped in a Supplier lambda so .get() is called LATER
                () -> PotionContents.createItemStack(Items.POTION, Potions.WATER),// Water Bottle input
                () -> new ItemStack(VibraniumBlocks.BLUE_GLOW_BERRIES.get()),// Blue Glow Berries ingredient
                () -> {
                    ItemStack alcohol = new ItemStack(VibraniumItems.BLUE_BERRY_SPIRIT.get());// Blue Berry Alcohol output

                    return alcohol;// Register recipe
                }
        );
    }

    @ExpectPlatform
    public static void registerPotionToItemRecipe(
            Supplier<ItemStack> inputPotion,
            Supplier<ItemStack> ingredient,
            Supplier<ItemStack> output
    ) {
        throw new AssertionError();
    }
}
