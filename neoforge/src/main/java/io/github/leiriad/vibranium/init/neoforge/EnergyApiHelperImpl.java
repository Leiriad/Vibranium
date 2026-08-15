package io.github.leiriad.vibranium.init.neoforge;

import io.github.leiriad.vibranium.entity.ElectricWireEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    // Check if an energy storage can act as a source (supports extraction)
    public static boolean isEnergySource(Level level, BlockPos pos, Direction side) {
        var targetStorage = level.getCapability(Capabilities.Energy.BLOCK, pos, side.getOpposite());
        BlockEntity targetEntity = level.getBlockEntity(pos);
        if (targetEntity instanceof ElectricWireEntity) {
            return false;
        }
        if (targetStorage != null) {
            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = targetStorage.extract(1, transaction);
                return extracted > 0;
            }
        }
        return false;
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
