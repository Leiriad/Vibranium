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
import vibranium.block.VibraniumGrassBlock;
import vibranium.block.VibraniumPathBlock;
import vibranium.block.VibraniumDirt;
import vibranium.block.VibraniumOre;

import java.util.function.Function;

public class VibraniumBlocks {
    public static Block VIBRANIUM_ORE;
    public static Block VIBRANIUM_DIRT;
    public static Block VIBRANIUM_GRASS_BLOCK;
    public static Block VIBRANIUM_PATH;

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(
                (itemGroup) -> {
                    itemGroup.accept(VIBRANIUM_ORE.asItem());
                    itemGroup.accept(VIBRANIUM_DIRT.asItem());
                    itemGroup.accept(VIBRANIUM_GRASS_BLOCK.asItem());
                    itemGroup.accept(VIBRANIUM_PATH.asItem());


        });
    }

    private static BlockBehaviour.Properties baseVibraniumDirtSettings() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)
                .strength(0.5f, 0.5f)
                .sound(SoundType.GRASS)
                .instrument(NoteBlockInstrument.BASEDRUM).randomTicks();
    }

   public static void registerModBlocks() {
        //Vibranium Ore
        VIBRANIUM_ORE = register(
                "vibranium_ore",
                VibraniumOre::new,
                BlockBehaviour.Properties.of()
                        .strength(3f, 10f)
                        .requiresCorrectToolForDrops()
                        .instrument(NoteBlockInstrument.CHIME),
                true
        );
        VIBRANIUM_DIRT = register(
                "vibranium_dirt",
                VibraniumDirt::new,
                baseVibraniumDirtSettings().sound(SoundType.GRAVEL),
                true
        );
        VIBRANIUM_GRASS_BLOCK = register(
                "vibranium_grass_block",
                VibraniumGrassBlock::new,
                baseVibraniumDirtSettings(),
                true
        );
        VIBRANIUM_PATH = register(
               "vibranium_path",
               VibraniumPathBlock::new,
               baseVibraniumDirtSettings(),
               true
        );

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