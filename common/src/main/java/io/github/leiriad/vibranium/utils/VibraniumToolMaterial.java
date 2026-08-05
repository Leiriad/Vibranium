package io.github.leiriad.vibranium.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.tags.TagKey;

public class VibraniumToolMaterial {

    public static final TagKey<Item> VIBRANIUM_TOOL_MATERIALS = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath("vibranium", "vibranium_tool_materials")
    );

    // Vibranium material instance
    public static final ToolMaterial VIBRANIUM = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL, // Unmineable blocks
            2500,                                   // Durability
            10.0F,                                  // Mining speed
            5.0F,                                   // Base attack damage bonus
            20,                                     // Enchantability rating
            VIBRANIUM_TOOL_MATERIALS                // Repair items tag
    );

}