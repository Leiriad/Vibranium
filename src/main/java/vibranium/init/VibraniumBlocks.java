package vibranium.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vibranium.Vibranium;
import vibranium.block.VibraniumOre;

public class VibraniumBlocks {

    // 1. Définition du bloc
    public static final Block VIBRANIUM_ORE = registerBlock("vibranium_ore", new VibraniumOre(BlockBehaviour.Properties.of()));

    // Méthode générique pour enregistrer le bloc et son ItemBlock
    private static <T extends Block> T registerBlock(String name, T block) {
            Identifier identifier = Identifier.fromNamespaceAndPath(Vibranium.MOD_ID, name);

        // Enregistrement du bloc
        Registry.register(BuiltInRegistries.BLOCK, identifier, block);

        // Enregistrement de l'item correspondant
        Registry.register(BuiltInRegistries.ITEM, identifier, new BlockItem(block, new Item.Properties()));

        return block;
    }

    // Appelé lors de l'initialisation du mod
    public static void registerModBlocks() {
        Vibranium.LOGGER.info("Enregistrement des blocs pour " + Vibranium.MOD_ID);
    }
}