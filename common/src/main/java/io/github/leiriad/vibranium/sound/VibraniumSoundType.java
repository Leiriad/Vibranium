package io.github.leiriad.vibranium.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class VibraniumSoundType {
    public static final SoundType DEPLETED_VIBRANIUM_BLOCK = new SoundType(
            0.8f, // global volume
            0.5f, // Pitch
            SoundEvents.NETHERITE_BLOCK_BREAK,
            SoundEvents.NETHERITE_BLOCK_STEP,
            SoundEvents.NETHERITE_BLOCK_PLACE,
            SoundEvents.NETHERITE_BLOCK_HIT,
            SoundEvents.NETHERITE_BLOCK_FALL
    );
    public static final SoundType VIBRANIUM_BLOCK = new SoundType(
            1.0f, // global volume
            1.5f, // Pitch
            SoundEvents.NETHERITE_BLOCK_BREAK,
            SoundEvents.NETHERITE_BLOCK_STEP,
            SoundEvents.NETHERITE_BLOCK_PLACE,
            SoundEvents.NETHERITE_BLOCK_HIT,
            SoundEvents.NETHERITE_BLOCK_FALL
    );
    public static final SoundType DEPLETED_VIBRANIUM_GRATE = new SoundType(
            1f, // global volume
            1f, // Pitch
            SoundEvents.NETHERITE_BLOCK_BREAK,
            SoundEvents.NETHERITE_BLOCK_STEP,
            SoundEvents.NETHERITE_BLOCK_PLACE,
            SoundEvents.NETHERITE_BLOCK_HIT,
            SoundEvents.NETHERITE_BLOCK_FALL
    );
    public static final SoundType VIBRANIUM_GRATE = new SoundType(
            0.8f, // global volume
            0.5f, // Pitch
            SoundEvents.NETHERITE_BLOCK_BREAK,
            SoundEvents.NETHERITE_BLOCK_STEP,
            SoundEvents.NETHERITE_BLOCK_PLACE,
            SoundEvents.NETHERITE_BLOCK_HIT,
            SoundEvents.NETHERITE_BLOCK_FALL
    );
}
