package io.github.leiriad.vibranium.init;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class EnergyApiHelper {

    /**
     * Checks if an adjacent block has energy storage (generator or device).
     */
    @ExpectPlatform
    public static boolean isEnergyMachine(Level level, BlockPos pos, Direction side) {
        throw new AssertionError();
    }
    /**
     * Tries to push energy into an adjacent energy storage block.
     */
    @ExpectPlatform
    public static int distributeEnergy(Level level, BlockPos pos, Direction side, int maxTransfer) {
        throw new AssertionError();
    }

    /**
     * Pull energy from a neighboring block
     */
    @ExpectPlatform
    public static int pullEnergy(Level level, BlockPos pos, Direction side, int maxTransfer) {
        throw new AssertionError();
    }
}