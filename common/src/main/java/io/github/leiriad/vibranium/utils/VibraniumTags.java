package io.github.leiriad.vibranium.utils;

import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class VibraniumTags {
    public static class Items {
        public static final TagKey<Item> FLUID_BUCKET = TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "fluid_bucket")
        );
        public static final TagKey<Item> STEW_FLOWERS = TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "stew_flowers")
        );
        public static final TagKey<Item> BLUE_FLOWERS = TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "blue_flowers")
        );
        public static final TagKey<Item> ELECTRIC_WIRES = TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "electric_wires")
        );
    }
    public static class Blocks {
        public static final TagKey<Block> DEPLETED_VIBRANIUM = TagKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "depleted_vibranium")
        );
    }
}