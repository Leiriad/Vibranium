package io.github.leiriad.vibranium.event;

import dev.architectury.event.EventResult;
import io.github.leiriad.vibranium.item.BlackPantherArmorItem;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorKineticChargeHandler {

    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    // Flag to prevent infinite re-entry when manually applying reduced fall damage
    private static final ThreadLocal<Boolean> IS_REAPPLYING_FALL_DAMAGE = ThreadLocal.withInitial(() -> false);

    /**
     * Handles kinetic charge distribution across equipped Black Panther armor pieces when hit.
     */
    public static EventResult handleArmorCharge(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof Player player) || player.level().isClientSide() || amount <= 0.0F) {
            return EventResult.pass();
        }

        // Prevent recursive processing when re-applying reduced fall damage
        if (IS_REAPPLYING_FALL_DAMAGE.get()) {
            return EventResult.pass();
        }

        boolean isFallDamage = source.is(DamageTypeTags.IS_FALL);
        boolean hasPantherBoots = player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof BlackPantherArmorItem;

        // If it is fall damage without Panther boots equipped, apply standard damage
        if (isFallDamage && !hasPantherBoots) {
            return EventResult.pass();
        }

        // Bypass passive or environmental damage types for kinetic energy storage (except fall damage handled above)
        if (!isFallDamage && (source.is(DamageTypeTags.BYPASSES_ARMOR) || source.is(DamageTypeTags.IS_DROWNING))) {
            return EventResult.pass();
        }

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);

        // Check if chestplate is fully charged BEFORE storing new damage -> Trigger auto discharge!
        if (chestStack.getItem() instanceof BlackPantherArmorItem) {
            float chestCharge = chestStack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            if (chestCharge >= 100.0F && player.level() instanceof ServerLevel serverLevel) {
                BlackPantherArmorItem.triggerAutoDischarge(serverLevel, player, chestStack);
            }
        }

        // Collect worn suit pieces
        List<ItemStack> equippedPantherArmor = new ArrayList<>();
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof BlackPantherArmorItem) {
                equippedPantherArmor.add(stack);
            }
        }

        if (equippedPantherArmor.isEmpty()) {
            return EventResult.pass();
        }

        // Calculate absorbable damage base (70% conversion for fall damage, 100% for other combat sources)
        float absorbedAmount = isFallDamage ? (amount * 0.70F) : amount;

        // Generate total charge pool and distribute among equipped suit parts
        float totalGeneratedCharge = absorbedAmount * 2.0F;
        float chargePerPiece = totalGeneratedCharge / equippedPantherArmor.size();

        for (ItemStack armorStack : equippedPantherArmor) {
            float currentCharge = armorStack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            if (currentCharge < 100.0F) {
                float updatedCharge = Math.min(100.0F, currentCharge + chargePerPiece);
                armorStack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), updatedCharge);
            }
        }

        // Handle partial fall damage mitigation (30% remaining damage applied to player)
        if (isFallDamage) {
            float remainingDamage = amount * 0.30F;
            if (remainingDamage > 0.0F) {
                IS_REAPPLYING_FALL_DAMAGE.set(true);
                try {
                    player.hurt(source, remainingDamage);
                } finally {
                    IS_REAPPLYING_FALL_DAMAGE.set(false);
                }
            }
            return EventResult.interruptFalse(); // Cancel initial 100% fall damage event
        }

        return EventResult.pass();
    }
}