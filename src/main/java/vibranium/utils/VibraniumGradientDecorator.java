package vibranium.utils;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import vibranium.init.VibraniumBlocks;
import vibranium.init.VibraniumTreeDecorators;

import java.util.Comparator;
import java.util.List;

public class VibraniumGradientDecorator extends TreeDecorator {
    public static final MapCodec<VibraniumGradientDecorator> CODEC = MapCodec.unit(() -> VibraniumGradientDecorator.INSTANCE);
    public static final VibraniumGradientDecorator INSTANCE = new VibraniumGradientDecorator();

    @Override
    protected TreeDecoratorType<?> type() {
        // Tu devras enregistrer ce type dans ton registre de TreeDecoratorTypes
        return VibraniumTreeDecorators.GRADIENT_DECORATOR;
    }

    @Override
    public void place(Context context) {
        List<BlockPos> leaves = context.leaves();
        if (leaves.isEmpty()) return;

        // Trouver le point le plus haut et le plus bas du feuillage
        int maxY = leaves.stream().max(Comparator.comparingInt(BlockPos::getY)).get().getY();
        int minY = leaves.stream().min(Comparator.comparingInt(BlockPos::getY)).get().getY();
        int totalHeight = maxY - minY;

        for (BlockPos pos : leaves) {
            int relativeY = pos.getY() - minY;

            // Calcul du tiers (0.0 à 1.0)
            double percentile = (double) relativeY / (totalHeight > 0 ? totalHeight : 1);

            if (percentile > 0.7) { // Haut (30% du haut)
                context.setBlock(pos, VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.defaultBlockState());
            } else if (percentile > 0.3) { // Milieu
                context.setBlock(pos, VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.defaultBlockState());
            } else { // Bas
                context.setBlock(pos, VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.defaultBlockState());
            }
        }
    }
}
