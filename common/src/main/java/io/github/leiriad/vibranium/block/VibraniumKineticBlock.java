package io.github.leiriad.vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public interface VibraniumKineticBlock {

    /**
     * Checks if the player is holding a valid tool (Diamond Pickaxe or better) to safely manipulate the block.
     */
    default boolean isDiamondPickaxe(ItemStack stack) {
        return stack.is(Items.DIAMOND_PICKAXE) || stack.is(Items.NETHERITE_PICKAXE);
    }

    /**
     * Triggers a kinetic shockwave repulsion on the target entity.
     */
    default void triggerKineticReaction(ServerLevel level, BlockPos pos, LivingEntity target, double forceMultiplier, Vec3 overrideDirection) {
        if (target.invulnerableTime > 0) return;

        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 pushDir = (overrideDirection != null) ? overrideDirection : target.position().subtract(center).normalize();

        // Visual and Sound effects
        level.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y + 0.2, center.z, 1, 0.0, 0.0, 0.0, 0.0);
        level.playSound(null, pos, SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.BLOCKS, 0.8F, 1.6F);

        // Apply kinetic damage (adjust the 4.0F float value to scale damage)
        target.hurt(level.damageSources().flyIntoWall(), 4.0F * (float) forceMultiplier);

        // Apply knockback impulse
        double force = 0.8 * forceMultiplier;
        Vec3 velocity = new Vec3(pushDir.x * force, 0.4 * forceMultiplier, pushDir.z * force);

        target.setDeltaMovement(target.getDeltaMovement().add(velocity));
        target.hurtMarked = true;
        target.invulnerableTime = 10;
    }
}