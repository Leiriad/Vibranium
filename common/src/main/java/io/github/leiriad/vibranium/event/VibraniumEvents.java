package io.github.leiriad.vibranium.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import io.github.leiriad.vibranium.item.VibraniumSpear;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VibraniumEvents {
    public static void registerEvents() {
        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            if (entity instanceof Player player) {
                ItemStack mainHand = player.getMainHandItem();
                if (mainHand.getItem() instanceof VibraniumSpear) {
                    //Ignore passive damages
                    if (source.is(DamageTypeTags.BYPASSES_ARMOR) ||
                            source.is(DamageTypeTags.IS_DROWNING) ||
                            source.is(DamageTypeTags.IS_FALL) ||
                            source.is(DamageTypeTags.IS_FIRE) ||
                            source.is(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)){
                        return EventResult.pass();
                    }

                    float currentCharge = mainHand.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
                    float newCharge = Math.min(100.0F, currentCharge + (amount * 5.0F));
                    mainHand.set(VibraniumDataComponents.KINETIC_CHARGE.get(), newCharge);

                    // Visual and sound effects
                    if (!player.level().isClientSide()) {
                        ServerLevel serverLevel = (ServerLevel) player.level();
                        serverLevel.sendParticles(
                                ParticleTypes.WITCH, // Particles
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
            }
            return EventResult.pass();
        });
    }
}
