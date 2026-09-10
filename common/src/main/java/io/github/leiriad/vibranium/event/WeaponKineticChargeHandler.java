package io.github.leiriad.vibranium.event;

import dev.architectury.event.EventResult;
import io.github.leiriad.vibranium.item.VibraniumAxe;
import io.github.leiriad.vibranium.item.VibraniumSpear;
import io.github.leiriad.vibranium.item.VibraniumSword;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WeaponKineticChargeHandler {

    /**
     * Handles kinetic charge accumulation for Vibranium weapons when the player takes damage.
     */
    public static EventResult handleWeaponCharge(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof Player player) || player.level().isClientSide() || amount <= 0.0F) {
            return EventResult.pass();
        }

        //Sword parry block mechanism (Active guard)
        ItemStack activeUseItem = player.getUseItem();
        if (player.isUsingItem() && activeUseItem.getItem() instanceof VibraniumSword) {
            if (!isEnvironmentalDamage(source)) {
                applyWeaponCharge(player, activeUseItem, amount * 5.0F);
                // Play shield impact sound when parrying
                player.level().playSound(
                        (Player) null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.IRON_BREAK,
                        SoundSource.PLAYERS,
                        1.0F, 1.2F
                );
                // Cancel damage taken during parry block
                return EventResult.interruptFalse();
            }
        }

        // Passive charge accumulation for held weapons (Main hand)
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof VibraniumSpear || mainHand.getItem() instanceof VibraniumSword || mainHand.getItem() instanceof VibraniumAxe) {
            if (isEnvironmentalDamage(source)) {
                return EventResult.pass();
            }

            applyWeaponCharge(player, mainHand, amount * 5.0F);
        }

        return EventResult.pass();
    }

    /**
     * Helper to increase kinetic charge and trigger visual/audio feedback.
     */
    private static void applyWeaponCharge(Player player, ItemStack stack, float chargeAmount) {
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        float newCharge = Math.min(100.0F, currentCharge + chargeAmount);
        stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), newCharge);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.WITCH,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    8, 0.3, 0.5, 0.3, 0.1
            );
            serverLevel.playSound(
                    null, player.blockPosition(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS,
                    0.8F, 1.5F
            );
        }
    }

    private static boolean isEnvironmentalDamage(DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_ARMOR) ||
                source.is(DamageTypeTags.IS_DROWNING) ||
                source.is(DamageTypeTags.IS_FALL) ||
                source.is(DamageTypeTags.IS_FIRE) ||
                source.is(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES);
    }
}