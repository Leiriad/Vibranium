package vibranium.init;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import vibranium.Vibranium;
import vibranium.block.*;

import java.util.function.Function;

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

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(
                (itemGroup) -> {
                    itemGroup.accept(VIBRANIUM_ORE.asItem());
                    itemGroup.accept(VIBRANIUM_DIRT.asItem());
                    itemGroup.accept(VIBRANIUM_GRASS_BLOCK.asItem());
                    itemGroup.accept(BLACKGRAVEL.asItem());
                    itemGroup.accept(BLACKCLAY.asItem());
                    itemGroup.accept(PURPLE_SHORT_GRASS.asItem());
                    itemGroup.accept(PURPLE_TALL_GRASS.asItem());
                    itemGroup.accept(PURPLE_AZALEA.asItem());
        });
    }

    public static void registerModBlocks() {
        //Vibranium Ore
        VIBRANIUM_ORE = register(
                "vibranium_ore",
                VibraniumOre::new,
                VibraniumOre.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        VIBRANIUM_DIRT = register(
                "vibranium_dirt",
                VibraniumDirt::new,
                VibraniumDirt.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        VIBRANIUM_GRASS_BLOCK = register(
                "vibranium_grass_block",
                VibraniumGrassBlock::new,
                VibraniumGrassBlock.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        VIBRANIUM_PATH = register(
               "vibranium_path",
               VibraniumPathBlock::new,
                VibraniumPathBlock.getProperties(BlockBehaviour.Properties.of()),
               true
        );
        VIBRANIUM_FARMLAND = register(
               "vibranium_farmland",
               VibraniumFarmland::new,
                VibraniumFarmland.getProperties(BlockBehaviour.Properties.of()),
               true
        );
        BLACKGRAVEL = register(
               "blackgravel",
               BlackGravel::new,
                BlackGravel.getProperties(BlockBehaviour.Properties.of()),
               true
        );
        BLACKCLAY = register(
                "blackclay",
                BlackClay::new,
                BlackClay.getProperties(BlockBehaviour.Properties.of()),
                true
        );
        PURPLE_SHORT_GRASS = register(
                "purple_short_grass",
                PurpleShortGrass::new,
                PurpleShortGrass.getProperties(BlockBehaviour.Properties.of()),
                true);
        PURPLE_TALL_GRASS = register(
                "purple_tall_grass",
                PurpleTallGrass::new,
                PurpleTallGrass.getProperties(BlockBehaviour.Properties.of()),
                true);
        PURPLE_AZALEA= register(
                "purple_azalea",
                PurpleAzalea::new,
                PurpleAzalea.getProperties(BlockBehaviour.Properties.of()),
                true);

    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        // Create a registry key for the block
        ResourceKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings.setId(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            ResourceKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }


        private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Vibranium.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Vibranium.MOD_ID, name));
    }


}