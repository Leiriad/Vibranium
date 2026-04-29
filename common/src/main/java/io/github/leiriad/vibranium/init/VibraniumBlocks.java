package io.github.leiriad.vibranium.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.block.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import io.github.leiriad.vibranium.block.*;
import net.minecraft.world.item.CreativeModeTabs;


import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class VibraniumBlocks {
    public static Block BLACKGRAVEL;
    public static Block VIBRANIUM_ORE;
    public static Block VIBRANIUM_DIRT;
    public static Block VIBRANIUM_GRASS_BLOCK;
    public static Block VIBRANIUM_PATH;
    public static Block VIBRANIUM_FARMLAND;
    public static Block BLACKCLAY;
    public static Block PURPLE_SHORT_GRASS;
    public static Block PURPLE_TALL_GRASS;
    public static Block PURPLE_AZALEA;
    public static Block PURPLE_AZALEA_LEAVES_VIOLET;
    public static Block PURPLE_AZALEA_LEAVES_DARK_BLUE;
    public static Block PURPLE_AZALEA_LEAVES_CYAN;
    public static Block FLOWERING_PURPLE_AZALEA;
    public static Block FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET;
    public static Block FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE;
    public static Block FLOWERING_PURPLE_AZALEA_LEAVES_CYAN;
    public static Block PURPLE_MOSS_BLOCK;
    public static Block PURPLE_MOSS_CARPET;
    public static Block PURPLE_CAVE_VINES;
    public static Block PURPLE_CAVE_VINES_PLANT;
    public static Block POTTED_PURPLE_AZALEA_BUSH;
    public static Block POTTED_FLOWERING_PURPLE_AZALEA_BUSH;
    public static Block BIG_PURPLE_DRIPLEAF;
    public static Block BIG_PURPLE_DRIPLEAF_STEM;
    public static Block SMALL_PURPLE_DRIPLEAF;
    public static Block PURPLE_VINE;
    public static Block HEART_SHAPED_HERB;

    private static final RegistrarManager REGISTRIES = RegistrarManager.get(VibraniumMod.MOD_ID);
    public static final Registrar<Block> BLOCKS = REGISTRIES.get(Registries.BLOCK);
    public static final Registrar<Item> ITEMS = REGISTRIES.get(Registries.ITEM);

    ///Registers block in the game
    public static void registerModBlocks() {

        VIBRANIUM_ORE = register(
                "vibranium_ore",
                VibraniumOre::new,
                () -> VibraniumOre.getProperties(BlockBehaviour.Properties.of()),//Use of lambda to provide a supplier instead of direct properties to lazy load in order to way for Minecraft data
                true 
        );

        VIBRANIUM_DIRT = register(
                "vibranium_dirt",
                VibraniumDirt::new,
                ()-> VibraniumDirt.getProperties(BlockBehaviour.Properties.of()),
                true 
        );
        VIBRANIUM_GRASS_BLOCK = register(
                "vibranium_grass_block",
                VibraniumGrassBlock::new,
                ()->VibraniumGrassBlock.getProperties(BlockBehaviour.Properties.of()),
                true 
        );
        VIBRANIUM_PATH = register(
               "vibranium_path",
               VibraniumPathBlock::new,
                () ->VibraniumPathBlock.getProperties(BlockBehaviour.Properties.of()),
               true
        );
        VIBRANIUM_FARMLAND = register(
               "vibranium_farmland",
               VibraniumFarmland::new,
                ()->VibraniumFarmland.getProperties(BlockBehaviour.Properties.of()),
               true
        );
        BLACKGRAVEL = register(
               "blackgravel",
               BlackGravel::new,
                ()->BlackGravel.getProperties(BlockBehaviour.Properties.of()),
               true 
        );
        BLACKCLAY = register(
                "blackclay",
                BlackClay::new,
                ()->BlackClay.getProperties(BlockBehaviour.Properties.of()),
                true 
        );
        PURPLE_SHORT_GRASS = register(
                "purple_short_grass",
                PurpleShortGrass::new,
                ()->PurpleShortGrass.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_TALL_GRASS = register(
                "purple_tall_grass",
                PurpleTallGrass::new,
                ()->PurpleTallGrass.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_AZALEA_LEAVES_VIOLET= register(
                "purple_azalea_leaves_violet",
                PurpleAzaleaLeavesViolet::new,
                ()->PurpleAzaleaLeavesViolet.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_AZALEA_LEAVES_DARK_BLUE= register(
                "purple_azalea_leaves_dark_blue",
                PurpleAzaleaLeavesDarkBlue::new,
                ()->PurpleAzaleaLeavesDarkBlue.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_AZALEA_LEAVES_CYAN= register(
                "purple_azalea_leaves_cyan",
                PurpleAzaleaLeavesCyan::new,
                ()->PurpleAzaleaLeavesCyan.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_AZALEA= register(
                "purple_azalea",
                PurpleAzalea::new,
                ()->PurpleAzalea.getProperties(BlockBehaviour.Properties.of()),
                true);
        FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET= register(
                "flowering_purple_azalea_leaves_violet",
                FloweringPurpleAzaleaLeavesViolet::new,
                ()->FloweringPurpleAzaleaLeavesViolet.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE= register(
                "flowering_purple_azalea_leaves_dark_blue",
                FloweringPurpleAzaleaLeavesDarkBlue::new,
                ()->FloweringPurpleAzaleaLeavesDarkBlue.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        FLOWERING_PURPLE_AZALEA_LEAVES_CYAN= register(
                "flowering_purple_azalea_leaves_cyan",
                FloweringPurpleAzaleaLeavesCyan::new,
                ()->FloweringPurpleAzaleaLeavesCyan.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        FLOWERING_PURPLE_AZALEA= register(
                "flowering_purple_azalea",
                FloweringPurpleAzalea::new,
                ()->FloweringPurpleAzalea.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_MOSS_BLOCK= register(
                "purple_moss_block",
                PurpleMossBlock::new,
                ()->PurpleMossBlock.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_MOSS_CARPET= register(
                "purple_moss_carpet",
                PurpleMossCarpet::new,
                ()->PurpleMossCarpet.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_CAVE_VINES_PLANT= register(
                "purple_cave_vines_plant",
                PurpleCaveVinesPlant::new,
                ()->PurpleCaveVinesPlant.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_CAVE_VINES= register(
                "purple_cave_vines",
                PurpleCaveVines::new,
                ()->PurpleCaveVines.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        POTTED_PURPLE_AZALEA_BUSH= register(
                "potted_purple_azalea_bush",
                (props) -> new PottedPurpleAzalea(
                        BLOCKS.get(Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "purple_azalea")),
                        props
                ),
                ()->PottedPurpleAzalea.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        POTTED_FLOWERING_PURPLE_AZALEA_BUSH= register(
                "potted_flowering_purple_azalea_bush",
                (props) -> new PottedFloweringPurpleAzalea(
                        BLOCKS.get(Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "flowering_purple_azalea")),
                        props
                ),
                ()->PottedFloweringPurpleAzalea.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        BIG_PURPLE_DRIPLEAF_STEM = register(
                "big_purple_dripleaf_stem",
                BigPurpleDripleafStem::new,
                ()->BigPurpleDripleafStem.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        BIG_PURPLE_DRIPLEAF = register(
                "big_purple_dripleaf",
                BigPurpleDripleaf::new,
                ()->BigPurpleDripleaf.getProperties(BlockBehaviour.Properties.of()),
                true
        );

        SMALL_PURPLE_DRIPLEAF = register(
                "small_purple_dripleaf",
                SmallPurpleDripleaf::new,
                ()->SmallPurpleDripleaf.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_VINE = register(
                "purple_vine",
                PurpleVine::new,
                ()->PurpleVine.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        HEART_SHAPED_HERB = register(
                "heart_shaped_herb",
                HeartShapedHerb::new,
                ()->HeartShapedHerb.getProperties(BlockBehaviour.Properties.of()),
                true
        );
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, Supplier<BlockBehaviour.Properties> settingsSupplier, boolean shouldRegisterItem) {
        // Create a registry key for the block
        Identifier id = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, name);
        // Use supplier to let the game choose when it creates the block instance
        System.out.println("Enregistrement de : " + name);
        var blockSupplier = BLOCKS.register(id, () -> blockFactory.apply(settingsSupplier.get()));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // IMPORTANT : BlockItem should be created just before being called
            ITEMS.register(id, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
        }

        return blockSupplier.get();
    }
    //Add objets to creative tabs
    public static void addItemsToTabs() {
        //Natural blocks
        CreativeTabRegistry.appendStack(CreativeModeTabs.NATURAL_BLOCKS,
                () -> new ItemStack(getItem("vibranium_ore")),
                () -> new ItemStack(getItem("vibranium_dirt")),
                () -> new ItemStack(getItem("vibranium_grass_block")),
                () -> new ItemStack(getItem("blackgravel")),
                () -> new ItemStack(getItem("blackclay")),
                () -> new ItemStack(getItem("purple_short_grass")),
                () -> new ItemStack(getItem("purple_tall_grass")),
                () -> new ItemStack(getItem("purple_azalea")),
                () -> new ItemStack(getItem("purple_azalea_leaves_cyan")),
                () -> new ItemStack(getItem("purple_azalea_leaves_dark_blue")),
                () -> new ItemStack(getItem("purple_azalea_leaves_violet")),
                () -> new ItemStack(getItem("flowering_purple_azalea")),
                () -> new ItemStack(getItem("flowering_purple_azalea_leaves_cyan")),
                () -> new ItemStack(getItem("flowering_purple_azalea_leaves_dark_blue")),
                () -> new ItemStack(getItem("flowering_purple_azalea_leaves_violet")),
                () -> new ItemStack(getItem("purple_moss_block")),
                () -> new ItemStack(getItem("purple_moss_carpet")),
                () -> new ItemStack(getItem("big_purple_dripleaf")),
                () -> new ItemStack(getItem("small_purple_dripleaf")),
                () -> new ItemStack(getItem("purple_vine")),
                () -> new ItemStack(getItem("heart_shaped_herb"))
        );

    }

    private static net.minecraft.world.item.Item getItem(String name) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(io.github.leiriad.vibranium.VibraniumMod.MOD_ID, name)
        ).map(net.minecraft.core.Holder::value).orElse(net.minecraft.world.item.Items.AIR);
    }
    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, name));
    }

}