package io.github.leiriad.vibranium.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class VibraniumTags {
    public static final TagKey<Item> FLUID_BUCKET = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath("vibranium", "fluid_bucket")
    );
}