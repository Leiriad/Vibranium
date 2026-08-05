package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.utils.VibraniumToolMaterial;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class VibraniumSpear extends Item {

    // Unique attribute modifier IDs
    private static final Identifier SPEAR_DAMAGE_ID = Identifier.fromNamespaceAndPath("vibranium", "spear_damage");
    private static final Identifier SPEAR_SPEED_ID = Identifier.fromNamespaceAndPath("vibranium", "spear_speed");
    private static final Identifier SPEAR_REACH_ID = Identifier.fromNamespaceAndPath("vibranium", "spear_reach");

    public VibraniumSpear(Properties properties) {
        super(properties);
    }

    public static Item.Properties getProperties(Item.Properties settings) {
        return settings.spear(
                VibraniumToolMaterial.VIBRANIUM,
                0.8F,  // f: attack duration
                1.2F,  // g: charge damage multiplier
                0.2F,  // h: minimum charge duration (manquant précédemment)
                0.5F,  // i: attacker speed threshold 1 duration
                1.0F,  // j: attacker speed threshold 1 value
                0.5F,  // k: attacker speed threshold 2 duration
                1.0F,  // l: attacker speed threshold 2 value
                0.5F,  // m: relative speed threshold duration
                1.0F   // n: relative speed threshold value
        );
    }

    private static ItemAttributeModifiers createSpearAttributes() {
        return ItemAttributeModifiers.builder()
                // Base attack damage
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(SPEAR_DAMAGE_ID, 6.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                // Attack speed
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(SPEAR_SPEED_ID, -2.8, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                // Extended reach (the signature spear attribute)
                .add(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(SPEAR_REACH_ID, 1.5, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}