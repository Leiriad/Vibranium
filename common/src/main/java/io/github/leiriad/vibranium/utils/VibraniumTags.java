package io.github.leiriad.vibranium.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class VibraniumTags {
    public static class Items {
        public static final TagKey<Item> FLUID_BUCKET = TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath("vibranium", "fluid_bucket")
        );
        public static final TagKey<Item> STEW_FLOWERS = TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath("vibranium", "stew_flowers")
        );
        public static final TagKey<Item> BLUE_FLOWERS = TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath("vibranium", "blue_flowers")
        );
    }
}