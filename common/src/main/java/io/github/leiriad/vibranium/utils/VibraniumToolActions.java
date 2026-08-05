package io.github.leiriad.vibranium.utils;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VibraniumToolActions {
    //BLOCKS
    ///Vibranium path block self-managed transition to dirt/grass
    public static BlockState getPathState(BlockState state){
        if (state.is(VibraniumBlocks.VIBRANIUM_DIRT.get())
                || state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get())){
            return VibraniumBlocks.VIBRANIUM_PATH.get().defaultBlockState();
        }
        return null;
    }
    ///Vibranium farmland block self-managed transition to dirt/grass
    public static BlockState getFarmlandState(BlockState state) {
        if (state.is(VibraniumBlocks.VIBRANIUM_DIRT.get())
                || state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get())) {
            return VibraniumBlocks.VIBRANIUM_FARMLAND.get().defaultBlockState();
        }
        return null;
    }
    //COMBAT
    public static void spawnShockwave(ServerLevel level, Vec3 center, float radius, float force, Player attacker) {
        // VISUAL EFFECTS (Expanding particle ring)
        int points = 36; // Number of particle steps to form a circle
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;

            // Outward particle velocities for expansion effect
            level.sendParticles(
                    ParticleTypes.EXPLOSION,
                    center.x + dx, center.y + 0.2, center.z + dz,
                    1, dx * 0.1, 0.05, dz * 0.1, 0.05
            );
        }

        // Play shockwave sound at the origin point
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.0F, 1.2F);

        // PHYSICAL EFFECTS (Knockback and impact damage on surrounding entities)
        double diameter = radius * 2.0;
        AABB area = AABB.ofSize(center, diameter, diameter, diameter);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != attacker && e.isAlive());

        for (LivingEntity target : targets) {
            Vec3 direction = target.position().subtract(center);
            double distance = direction.length();

            if (distance <= radius && distance > 0.001) {
                // Proportional knockback calculation based on distance
                Vec3 knockback = direction.normalize().scale((1.0 - (distance / radius)) * force);

                // Apply impulse force (upward lift + horizontal push)
                target.setDeltaMovement(target.getDeltaMovement().add(knockback.x, 0.35, knockback.z));
                target.hurtMarked = true; // Ensures network sync with clients
            }
        }
    }
}
