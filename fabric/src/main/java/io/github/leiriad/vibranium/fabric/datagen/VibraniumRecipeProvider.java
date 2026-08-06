package io.github.leiriad.vibranium.fabric.datagen;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumItems;
import io.github.leiriad.vibranium.utils.VibraniumTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

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
                //FURNACE
                // Main recipe configuration for autonomous furnace smelting
                // Vibranium dust to depleted vibranium ingot
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
                //Black gravel to vibranium glass
                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(VibraniumBlocks.BLACK_GRAVEL.get()),
                                RecipeCategory.BUILDING_BLOCKS,
                                VibraniumBlocks.VIBRANIUM_GLASS.get(),
                                0.1F,
                                200
                        )
                        .unlockedBy(getHasName(VibraniumBlocks.BLACK_GRAVEL.get()), has(VibraniumBlocks.BLACK_GRAVEL.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "glass_from_black_gravel")));
                //Black clay balls to black bricks
                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(VibraniumItems.BLACK_CLAY_BALL.get()),
                                RecipeCategory.MISC,
                                VibraniumItems.BLACK_BRICK.get(),
                                0.1F,
                                200
                        )
                        .unlockedBy(getHasName(VibraniumItems.BLACK_CLAY_BALL.get()), has(VibraniumItems.BLACK_CLAY_BALL.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_brick_from_smelting_black_clay_balls")));
                //Black clay to black terracota
                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(VibraniumBlocks.BLACK_CLAY.get()),
                                RecipeCategory.BUILDING_BLOCKS,
                                Items.BLACK_TERRACOTTA,
                                0.1F,
                                200
                        )
                        .unlockedBy(getHasName(VibraniumBlocks.BLACK_CLAY.get()), has(VibraniumBlocks.BLACK_CLAY.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_terracotta_from_smelting_black_clay")));

                //STONECUTTER
                //Black Brick Stairs
                SingleItemRecipeBuilder.stonecutting(
                                Ingredient.of(VibraniumBlocks.BLACK_BRICKS.get()),
                                RecipeCategory.BUILDING_BLOCKS,
                                VibraniumBlocks.BLACK_BRICK_STAIRS.get(),
                                1
                        )
                        .unlockedBy(getHasName(VibraniumBlocks.BLACK_BRICKS.get()), has(VibraniumBlocks.BLACK_BRICKS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_brick_stairs_from_black_bricks_stonecutting")));
                //Black Brick Slab
                SingleItemRecipeBuilder.stonecutting(
                                Ingredient.of(VibraniumBlocks.BLACK_BRICKS.get()),
                                RecipeCategory.BUILDING_BLOCKS,
                                VibraniumBlocks.BLACK_BRICK_SLAB.get(),
                                2
                        )
                        .unlockedBy(getHasName(VibraniumBlocks.BLACK_BRICKS.get()), has(VibraniumBlocks.BLACK_BRICKS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_brick_slab_from_black_bricks_stonecutting")));
                //Black Brick Wall
                SingleItemRecipeBuilder.stonecutting(
                                Ingredient.of(VibraniumBlocks.BLACK_BRICKS.get()),
                                RecipeCategory.BUILDING_BLOCKS,
                                VibraniumBlocks.BLACK_BRICK_WALL.get(),
                                1
                        )
                        .unlockedBy(getHasName(VibraniumBlocks.BLACK_BRICKS.get()), has(VibraniumBlocks.BLACK_BRICKS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_brick_wall_from_black_bricks_stonecutting")));

                //CRAFTING TABLE
                var itemHolderGetter = registryLookup.lookupOrThrow(Registries.ITEM);

                //Glass pane
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.DECORATIONS, VibraniumBlocks.VIBRANIUM_GLASS_PANE.get(), 16)
                        .pattern("GGG")
                        .pattern("GGG")
                        .define('G', VibraniumBlocks.VIBRANIUM_GLASS.get())
                        .unlockedBy(getHasName(VibraniumBlocks.VIBRANIUM_GLASS.get()), has(VibraniumBlocks.VIBRANIUM_GLASS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "vibranium_glass_pane")));
                //Reinforced vibranium glass
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.DECORATIONS, VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get(), 1)
                        .pattern("D D")
                        .pattern(" G ")
                        .pattern("D D")
                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())
                        .define('G', VibraniumBlocks.VIBRANIUM_GLASS.get())
                        .unlockedBy(getHasName(VibraniumBlocks.VIBRANIUM_GLASS.get()), has(VibraniumBlocks.VIBRANIUM_GLASS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "reinforced_vibranium_glass")));
                //Reinforced vibranium glass pane
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.DECORATIONS, VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS_PANE.get(), 16)
                        .pattern("GGG")
                        .pattern("GGG")
                        .define('G', VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get())
                        .unlockedBy(getHasName(VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()), has(VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "reinforced_vibranium_glass_pane")));
                //Fluid tank
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.MISC, VibraniumBlocks.FLUID_TANK.get())
                        .pattern("SGS")
                        .pattern("G G")
                        .pattern("SGS")
                        .define('S', Items.SLIME_BALL)
                        .define('G', VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS_PANE.get())

                        .unlockedBy(getHasName(VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS_PANE.get()), has(VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS_PANE.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "fluid_tank")));
                //Reactor hatch
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.MISC, VibraniumBlocks.REACTOR_HATCH.get())
                        .pattern("DSD")
                        .pattern("S S")
                        .pattern("DSD")
                        .define('S', Blocks.STONE)
                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())

                        .unlockedBy(getHasName(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get()), has(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "reactor_hatch")));
                // Reactor Pump (Requires a Fluid Tank as base + mechanical parts)
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.MISC, VibraniumBlocks.REACTOR_PUMP.get())
                        .pattern("DPR")
                        .pattern("DTB")
                        .pattern("DSD")
                        .define('P', Blocks.PISTON)                     // Mechanical pumping element
                        .define('R', Items.REDSTONE)
                        .define('T', VibraniumBlocks.FLUID_TANK.get()) //Fluid handling
                        .define('B', Items.BLAZE_POWDER) //Higher cost than fluid tank
                        .define('S', Blocks.STONE)

                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())

                        .unlockedBy(getHasName(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get()), has(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "reactor_pump")));
                // Reactor Outlet (Requires a Fluid Tank + Heat-resistant casing)
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.MISC, VibraniumBlocks.REACTOR_OUTLET.get())
                        .pattern("DGD")
                        .pattern("DTB")
                        .pattern("DRD")
                        .define('G', VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()) // Reinforced glass for high pressure/heat
                        .define('T', VibraniumBlocks.FLUID_TANK.get())               // Force fluid tank base
                        .define('B', Items.BLAZE_POWDER) //Higher cost than fluid tank
                        .define('R', Items.REDSTONE)                                 // Control logic
                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())

                        .unlockedBy(getHasName(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get()), has(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "reactor_outlet")));
                //Reactor control panel
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.MISC, VibraniumBlocks.REACTOR_CONTROL_PANEL.get())
                        .pattern("DSD")
                        .pattern("SGS")
                        .pattern("DSD")
                        .define('S', Blocks.STONE)
                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())
                        .define('G', Blocks.GLASS_PANE)

                        .unlockedBy(getHasName(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get()), has(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "reactor_control_panel")));
                //Reactor core
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.MISC, VibraniumBlocks.REACTOR_CORE.get())
                        .pattern("DGD")
                        .pattern("BDB")
                        .pattern("DGD")
                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())
                        .define('G', VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get())
                        .define('B', Blocks.DIAMOND_BLOCK) //stabilisation
                        .unlockedBy(getHasName(VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()), has(VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "reactor_core")));
                //Black clay balls to black clay
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.BUILDING_BLOCKS, VibraniumBlocks.BLACK_CLAY.get())
                        .pattern("CC")
                        .pattern("CC")
                        .define('C', VibraniumItems.BLACK_CLAY_BALL.get())
                        .unlockedBy(getHasName(VibraniumItems.BLACK_CLAY_BALL.get()), has(VibraniumItems.BLACK_CLAY_BALL.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_terracotta_from_black_clay_balls")));
                //Blue glow berry suspicious stew
                ShapelessRecipeBuilder.shapeless(itemHolderGetter, RecipeCategory.FOOD, Items.SUSPICIOUS_STEW)
                        .requires(Items.BOWL)
                        .requires(VibraniumTags.Items.STEW_FLOWERS)
                        .requires(VibraniumBlocks.BLUE_GLOW_BERRIES.get())
                        .unlockedBy(getHasName(VibraniumBlocks.BLUE_GLOW_BERRIES.get()), has(VibraniumBlocks.BLUE_GLOW_BERRIES.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "suspicious_stew_from_blue_glow_berries")));
                //Blue soup
                ShapelessRecipeBuilder.shapeless(itemHolderGetter, RecipeCategory.FOOD, VibraniumItems.BLUE_SOUP.get())
                        .requires(Items.BOWL)
                        .requires(VibraniumTags.Items.BLUE_FLOWERS)
                        .requires(VibraniumBlocks.BLUE_GLOW_BERRIES.get())
                        .unlockedBy(getHasName(VibraniumBlocks.BLUE_GLOW_BERRIES.get()), has(VibraniumBlocks.BLUE_GLOW_BERRIES.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "blue_soup_from_blue_glow_berries")));
                //Ancestral nectar
                ShapelessRecipeBuilder.shapeless(itemHolderGetter, RecipeCategory.FOOD, VibraniumItems.ANCESTRAL_NECTAR.get())
                        .requires(Items.BOWL)
                        .requires(VibraniumBlocks.BLUE_GLOW_BERRIES.get())
                        .requires(VibraniumItems.HEART_SHAPED_HERB.get())
                        .unlockedBy(getHasName(VibraniumItems.HEART_SHAPED_HERB.get()), has(VibraniumItems.HEART_SHAPED_HERB.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "ancestral_nectar_from_heart_shaped_herb")));
                //Black Bricks
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.BUILDING_BLOCKS, VibraniumBlocks.BLACK_BRICKS.get())
                        .pattern("BB")
                        .pattern("BB")
                        .define('B', VibraniumItems.BLACK_BRICK.get())
                        .unlockedBy(getHasName(VibraniumItems.BLACK_BRICK.get()), has(VibraniumItems.BLACK_BRICK.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_bricks_from_black_brick")));
                //Black Brick Stairs
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.BUILDING_BLOCKS, VibraniumBlocks.BLACK_BRICK_STAIRS.get())
                        .pattern("B  ")
                        .pattern("BB ")
                        .pattern("BBB")
                        .define('B', VibraniumBlocks.BLACK_BRICKS.get())
                        .unlockedBy(getHasName(VibraniumBlocks.BLACK_BRICKS.get()), has(VibraniumBlocks.BLACK_BRICKS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_brick_stairs_from_black_bricks")));
                //Black Brick Slab
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.BUILDING_BLOCKS, VibraniumBlocks.BLACK_BRICK_SLAB.get())
                        .pattern("BBB")
                        .define('B', VibraniumBlocks.BLACK_BRICKS.get())
                        .unlockedBy(getHasName(VibraniumBlocks.BLACK_BRICKS.get()), has(VibraniumBlocks.BLACK_BRICKS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_brick_slab_from_black_bricks")));
                //Black Brick Wall
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.BUILDING_BLOCKS, VibraniumBlocks.BLACK_BRICK_WALL.get(), 16)
                        .pattern("BBB")
                        .pattern("BBB")
                        .define('B', VibraniumBlocks.BLACK_BRICKS.get())
                        .unlockedBy(getHasName(VibraniumBlocks.BLACK_BRICKS.get()), has(VibraniumBlocks.BLACK_BRICKS.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "black_brick_wall_from_black_bricks")));
                // Vibranium spear
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.COMBAT, VibraniumItems.VIBRANIUM_SPEAR.get(), 1)
                        .pattern("  V")
                        .pattern(" D ")
                        .pattern("D  ")
                        .define('V', VibraniumItems.VIBRANIUM_INGOT.get())
                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())
                        .unlockedBy(getHasName(VibraniumItems.VIBRANIUM_INGOT.get()), has(VibraniumItems.VIBRANIUM_INGOT.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "vibranium_spear")));
                // Vibranium sword
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.COMBAT, VibraniumItems.VIBRANIUM_SWORD.get(), 1)
                        .pattern(" V ")
                        .pattern(" V ")
                        .pattern(" D ")
                        .define('V', VibraniumItems.VIBRANIUM_INGOT.get())
                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())
                        .unlockedBy(getHasName(VibraniumItems.VIBRANIUM_INGOT.get()), has(VibraniumItems.VIBRANIUM_INGOT.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "vibranium_sword")));
                // Vibranium shovel
                ShapedRecipeBuilder.shaped(itemHolderGetter, RecipeCategory.TOOLS, VibraniumItems.VIBRANIUM_SHOVEL.get(), 1)
                        .pattern(" V ")
                        .pattern(" D ")
                        .pattern(" D ")
                        .define('V', VibraniumItems.VIBRANIUM_INGOT.get())
                        .define('D', VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get())
                        .unlockedBy(getHasName(VibraniumItems.VIBRANIUM_INGOT.get()), has(VibraniumItems.VIBRANIUM_INGOT.get()))
                        .save(exporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("vibranium", "vibranium_shovel")));
            }

        };
    }

    @Override
    public String getName() {
        return "Vibranium Recipes";
    }
}