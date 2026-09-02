package io.github.leiriad.vibranium.client.soundmanagement;

import io.github.leiriad.vibranium.block.DepletedVibraniumBlock;
// N'oublie pas d'importer ta classe de grille ici
// import io.github.leiriad.vibranium.block.DepletedVibraniumGrateBlock;
import io.github.leiriad.vibranium.block.DepletedVibraniumGrate;
import io.github.leiriad.vibranium.utils.VibraniumTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Block;

public class VibraniumSoundFilter {

    public static float getVolumeMultiplierForInstance(SoundInstance sound) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) {
            return 1.0f;
        }

        SoundSource source = sound.getSource();
        Identifier identifier = sound.getIdentifier();
        String path = (identifier != null) ? identifier.getPath() : "";

        // 1. Rain and weather (checking above the player)
        if (source == SoundSource.WEATHER || path.contains("rain") || path.contains("thunder")) {
            return getRainAttenuationMultiplier(client);
        }

        // 2. Jukebox / Discs (RECORDS source)
        if (source == SoundSource.RECORDS) {
            Vec3 soundPos = new Vec3(sound.getX(), sound.getY(), sound.getZ());
            Vec3 playerPos = client.player.getEyePosition();

            // If the sound has a valid position
            if (!soundPos.equals(Vec3.ZERO)) {
                return calculateRaycastAttenuation(client, playerPos, soundPos);
            }
        }

        // 3. All other sounds positioned in the world (except UI/background music)
        if (!sound.isRelative()) {
            Vec3 soundPos = new Vec3(sound.getX(), sound.getY(), sound.getZ());
            Vec3 playerPos = client.player.getEyePosition();

            if (!soundPos.equals(Vec3.ZERO) && playerPos.distanceToSqr(soundPos) > 0.25) {
                return calculateRaycastAttenuation(client, playerPos, soundPos);
            }
        }

        return 1.0f;
    }

    /**
     * Checks if depleted Vibranium is located above the player's head.
     */
    private static float getRainAttenuationMultiplier(Minecraft client) {
        BlockPos playerPos = client.player.blockPosition();

        for (int i = 1; i <= 20; i++) {
            BlockPos checkPos = playerPos.above(i);
            Block block = client.level.getBlockState(checkPos).getBlock();

            if (block instanceof DepletedVibraniumBlock) {
                return 0.05f; // 95% attenuation
            } else if (block instanceof DepletedVibraniumGrate) {
                return 0.4f; // 60% attenuation
            }
        }
        return 1.0f;
    }

    /**
     * Direct raycast between the player's eyes and the centre of the emitting block.
     */
    private static float calculateRaycastAttenuation(Minecraft client, Vec3 playerPos, Vec3 soundPos) {
        double distance = playerPos.distanceTo(soundPos);
        int steps = (int) Math.ceil(distance * 2.0);

        if (steps == 0) return 1.0f;

        float currentMultiplier = 1.0f;

        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;
            Vec3 samplePoint = playerPos.lerp(soundPos, progress);
            BlockPos pos = BlockPos.containing(samplePoint);

            BlockState state = client.level.getBlockState(pos);
            float attenuation = getBlockAttenuation(state);

            currentMultiplier *= attenuation;

            // Early exit if sound is completely blocked
            if (currentMultiplier <= 0.05f) {
                return 0.05f;
            }
        }

        return Math.max(currentMultiplier, 0.05f);
    }

    private static float getBlockAttenuation(BlockState state) {
        // Check if the block belongs to the Depleted Vibranium tag
        if (isDepletedVibranium(state)) {

            // Handle open/close state for doors and trapdoors automatically
            if (state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN)) {
                return 1.0f; // Sound flows through open doors/trapdoors
            }
            if (state.hasProperty(TrapDoorBlock.OPEN) && state.getValue(TrapDoorBlock.OPEN)) {
                return 1.0f;
            }

            // Grate specific behavior
            if (state.getBlock() instanceof DepletedVibraniumGrate) {
                return 0.5f;
            }

            // Default closed block attenuation
            return 0.05f;
        }

        return 1.0f;
    }

    private static boolean isDepletedVibranium(BlockState state) {
        return state.is(VibraniumTags.Blocks.DEPLETED_VIBRANIUM);
    }
}