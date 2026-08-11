package io.github.leiriad.vibranium.init.fabric;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;

public class EnergyApiHelperImpl {
    // Check if an energy storage is available on that side
    public static boolean isEnergyMachine(Level level, BlockPos pos, Direction side) {
        return EnergyStorage.SIDED.find(level, pos, side.getOpposite()) != null;
    }

    // Distribute energy to a neighboring block using Fabric Transfer transactions
    public static int distributeEnergy(Level level, BlockPos pos, Direction side, int maxTransfer) {
        EnergyStorage targetStorage = EnergyStorage.SIDED.find(level, pos, side.getOpposite());
        if (targetStorage != null && targetStorage.supportsInsertion()) {
            // Open a transaction using Fabric's Transfer API
            try (Transaction transaction = Transaction.openOuter()) {
                long accepted = targetStorage.insert(maxTransfer, transaction);
                if (accepted > 0) {
                    transaction.commit();
                    return (int) accepted;
                }
            }
        }
        return 0;
    }

    // Pull energy from a neighboring block using Fabric Transfer transactions
    public static int pullEnergy(Level level, BlockPos pos, Direction side, int maxTransfer) {
        EnergyStorage targetStorage = EnergyStorage.SIDED.find(level, pos, side.getOpposite());
        if (targetStorage != null && targetStorage.supportsExtraction()) {
            try (Transaction transaction = Transaction.openOuter()) {
                long extracted = targetStorage.extract(maxTransfer, transaction);
                if (extracted > 0) {
                    transaction.commit();
                    return (int) extracted;
                }
            }
        }
        return 0;
    }
}
