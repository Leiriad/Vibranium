package io.github.leiriad.vibranium.init.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class EnergyApiHelperImpl {
    // Check if an energy storage is available on that side
    public static boolean isEnergyMachine(Level level, BlockPos pos, Direction side) {
        return level.getCapability(Capabilities.Energy.BLOCK, pos, side.getOpposite()) != null;
    }

    // Distribute energy to a neighboring block using NeoForge transactions
    public static int distributeEnergy(Level level, BlockPos pos, Direction side, int maxTransfer) {
        var targetStorage = level.getCapability(Capabilities.Energy.BLOCK, pos, side.getOpposite());
        if (targetStorage != null) {
            try (Transaction transaction = Transaction.openRoot()) {
                int accepted = targetStorage.insert(maxTransfer, transaction);
                if (accepted > 0) {
                    transaction.commit();
                    return accepted;
                }
            }
        }
        return 0;
    }
    // Pull energy from a neighboring block using NeoForge transactions
    public static int pullEnergy(Level level, BlockPos pos, Direction side, int maxTransfer) {
        var targetStorage = level.getCapability(Capabilities.Energy.BLOCK, pos, side.getOpposite());
        if (targetStorage != null) {
            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = targetStorage.extract(maxTransfer, transaction);
                if (extracted > 0) {
                    transaction.commit();
                    return extracted;
                }
            }
        }
        return 0;
    }
}
