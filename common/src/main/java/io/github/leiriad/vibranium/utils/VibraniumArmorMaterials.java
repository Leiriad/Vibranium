package io.github.leiriad.vibranium.utils;

import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import java.util.Map;

public class VibraniumArmorMaterials {

    // Defense values for each armor slot
    private static final Map<ArmorType, Integer> DEFENSE_VALUES = Map.of(
            ArmorType.HELMET, 4,
            ArmorType.CHESTPLATE, 9,
            ArmorType.LEGGINGS, 7,
            ArmorType.BOOTS, 4
    );

    // Equipment asset key pointing to the model JSON under assets/vibranium/equipment/black_panther.json
    public static final ResourceKey<EquipmentAsset> BLACK_PANTHER_ASSET = ResourceKey.create(
            EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "black_panther")
    );

    public static final ArmorMaterial BLACK_PANTHER = new ArmorMaterial(
            40, // Base durability multiplier (~2500 for chestplate)
            DEFENSE_VALUES,
            20, // Enchantability rating matching VibraniumToolMaterial
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.5F, // Toughness
            0.25F, // Knockback Resistance
            VibraniumToolMaterial.VIBRANIUM_TOOL_MATERIALS, // Repair tag
            BLACK_PANTHER_ASSET
    );
}