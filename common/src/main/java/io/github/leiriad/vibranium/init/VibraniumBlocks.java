package io.github.leiriad.vibranium.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.CreativeModeTabs;


import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class VibraniumBlocks {
    public static DeferredRegister<Block>BLOCKS = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.BLOCK);
    private static DeferredRegister<Item>ITEMS = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.ITEM);

    //CUSTOM BLOCKS & JOINED ITEMS
    //GROUND
    public static final RegistrySupplier<Block> VIBRANIUM_ORE = register("vibranium_ore", VibraniumOre::new, () -> VibraniumOre.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> VIBRANIUM_DIRT = register("vibranium_dirt", VibraniumDirt::new, () -> VibraniumDirt.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> VIBRANIUM_GRASS_BLOCK = register("vibranium_grass_block", VibraniumGrassBlock::new, () -> VibraniumGrassBlock.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> VIBRANIUM_PATH = register("vibranium_path", VibraniumPathBlock::new, () -> VibraniumPathBlock.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> VIBRANIUM_FARMLAND = register("vibranium_farmland", VibraniumFarmland::new, () -> VibraniumFarmland.getProperties(BlockBehaviour.Properties.of()), true);

    public static final RegistrySupplier<Block> BLACKGRAVEL = register("blackgravel", BlackGravel::new, () -> BlackGravel.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> BLACKCLAY = register("blackclay", BlackClay::new, () -> BlackClay.getProperties(BlockBehaviour.Properties.of()), true);

    //GRASS
    public static final RegistrySupplier<Block> PURPLE_SHORT_GRASS = register("purple_short_grass", PurpleShortGrass::new, () -> PurpleShortGrass.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> PURPLE_TALL_GRASS = register("purple_tall_grass", PurpleTallGrass::new, () -> PurpleTallGrass.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> PURPLE_MOSS_BLOCK = register("purple_moss_block", PurpleMossBlock::new, () -> PurpleMossBlock.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> PURPLE_MOSS_CARPET = register("purple_moss_carpet", PurpleMossCarpet::new, () -> PurpleMossCarpet.getProperties(BlockBehaviour.Properties.of()), true);

    //LEAVES
    public static final RegistrySupplier<Block> PURPLE_AZALEA_LEAVES_VIOLET = register("purple_azalea_leaves_violet", PurpleAzaleaLeavesViolet::new, () -> PurpleAzaleaLeavesViolet.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> PURPLE_AZALEA_LEAVES_DARK_BLUE = register("purple_azalea_leaves_dark_blue", PurpleAzaleaLeavesDarkBlue::new, () -> PurpleAzaleaLeavesDarkBlue.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> PURPLE_AZALEA_LEAVES_CYAN = register("purple_azalea_leaves_cyan", PurpleAzaleaLeavesCyan::new, () -> PurpleAzaleaLeavesCyan.getProperties(BlockBehaviour.Properties.of()), true);

    public static final RegistrySupplier<Block> FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET = register("flowering_purple_azalea_leaves_violet", FloweringPurpleAzaleaLeavesViolet::new, () -> FloweringPurpleAzaleaLeavesViolet.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE = register("flowering_purple_azalea_leaves_dark_blue", FloweringPurpleAzaleaLeavesDarkBlue::new, () -> FloweringPurpleAzaleaLeavesDarkBlue.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> FLOWERING_PURPLE_AZALEA_LEAVES_CYAN = register("flowering_purple_azalea_leaves_cyan", FloweringPurpleAzaleaLeavesCyan::new, () -> FloweringPurpleAzaleaLeavesCyan.getProperties(BlockBehaviour.Properties.of()), true);

    //BUSHES
    public static final RegistrySupplier<Block> PURPLE_AZALEA = register("purple_azalea", PurpleAzalea::new, () -> PurpleAzalea.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> FLOWERING_PURPLE_AZALEA = register("flowering_purple_azalea", FloweringPurpleAzalea::new, () -> FloweringPurpleAzalea.getProperties(BlockBehaviour.Properties.of()), true);

    //POTTED BUSHES
    public static final RegistrySupplier<Block> POTTED_PURPLE_AZALEA_BUSH = register(
            "potted_purple_azalea_bush",
            props -> new PottedPurpleAzalea(PURPLE_AZALEA.get(), props),
            () -> PottedPurpleAzalea.getProperties(BlockBehaviour.Properties.of()),
            false
    );

    public static final RegistrySupplier<Block> POTTED_FLOWERING_PURPLE_AZALEA_BUSH = register(
            "potted_flowering_purple_azalea_bush",
            props -> new PottedFloweringPurpleAzalea(FLOWERING_PURPLE_AZALEA.get(), props),
            () -> PottedFloweringPurpleAzalea.getProperties(BlockBehaviour.Properties.of()),
            false
    );

    //VINES
    public static final RegistrySupplier<Block> PURPLE_CAVE_VINES_PLANT = register("purple_cave_vines_plant", PurpleCaveVinesPlant::new, () -> PurpleCaveVinesPlant.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> PURPLE_CAVE_VINES = register("purple_cave_vines", PurpleCaveVines::new, () -> PurpleCaveVines.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> PURPLE_VINE = register("purple_vine", PurpleVine::new, () -> PurpleVine.getProperties(BlockBehaviour.Properties.of()), true);

    //DRIP LEAVES AND SPECIALS
    public static final RegistrySupplier<Block> BIG_PURPLE_DRIPLEAF_STEM = register("big_purple_dripleaf_stem", BigPurpleDripleafStem::new, () -> BigPurpleDripleafStem.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> BIG_PURPLE_DRIPLEAF = register("big_purple_dripleaf", BigPurpleDripleaf::new, () -> BigPurpleDripleaf.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> SMALL_PURPLE_DRIPLEAF = register("small_purple_dripleaf", SmallPurpleDripleaf::new, () -> SmallPurpleDripleaf.getProperties(BlockBehaviour.Properties.of()), true);
    public static final RegistrySupplier<Block> HEART_SHAPED_HERB = register("heart_shaped_herb", HeartShapedHerb::new, () -> HeartShapedHerb.getProperties(BlockBehaviour.Properties.of()), true);

    ///Registers block in the game
    public static void registerModBlocks() {
        BLOCKS.register();
        ITEMS.register();
    }

    private static <T extends Block> RegistrySupplier<T> register(String name, Function<BlockBehaviour.Properties, T> blockFactory, Supplier<BlockBehaviour.Properties> propertiesSupplier, boolean shouldRegisterItem) {
        Identifier id = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);

        // Bloc registering
        RegistrySupplier<T> blockSupplier = BLOCKS.register(id, () -> {
            BlockBehaviour.Properties props = propertiesSupplier.get();
            if (props == null) {
                throw new NullPointerException("Properties for " + name + " are null!");
            }
            props.setId(blockKey);
            return blockFactory.apply(props);
        });

        // BlockItem registering
        if (shouldRegisterItem) {
            ITEMS.register(id, () -> new BlockItem(
                    blockSupplier.get(),
                    new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))
            ));
        }

        return blockSupplier;
    }

    public static void addItemsToTabs() {
        //Add objets to creative tabs
        List.of(
                VIBRANIUM_ORE,
                VIBRANIUM_DIRT,
                VIBRANIUM_GRASS_BLOCK,
                BLACKGRAVEL,
                BLACKCLAY,
                PURPLE_SHORT_GRASS,
                PURPLE_TALL_GRASS,
                PURPLE_AZALEA,
                PURPLE_AZALEA_LEAVES_CYAN,
                PURPLE_AZALEA_LEAVES_DARK_BLUE,
                PURPLE_AZALEA_LEAVES_VIOLET,
                FLOWERING_PURPLE_AZALEA,
                FLOWERING_PURPLE_AZALEA_LEAVES_CYAN,
                FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE,
                FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET,
                PURPLE_MOSS_BLOCK,
                PURPLE_MOSS_CARPET,
                BIG_PURPLE_DRIPLEAF,
                SMALL_PURPLE_DRIPLEAF,
                PURPLE_VINE,
                HEART_SHAPED_HERB
        ).forEach(blockSupplier -> {
            CreativeTabRegistry.appendStack(CreativeModeTabs.NATURAL_BLOCKS, () -> new ItemStack(blockSupplier.get()));
        });
    }

}