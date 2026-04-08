package vibranium.utils;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
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
        return VibraniumTreeDecorators.GRADIENT_DECORATOR;
    }

    @Override
    public void place(Context context) {
        List<BlockPos> leaves = context.leaves();
        if (leaves.isEmpty()) return;

        //Getting higher and lower point of the tree leaves
        int maxY = leaves.stream().max(Comparator.comparingInt(BlockPos::getY)).get().getY();
        int minY = leaves.stream().min(Comparator.comparingInt(BlockPos::getY)).get().getY();
        int totalHeight = maxY - minY;

        //Find the first leaf to figure out if it's a flowering tree
        boolean isFlowering = context.level().isStateAtPosition(
                leaves.get(0),
                (state) -> state.is(VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE)
        );

        //Replace the placeholder leaves by the real ones
        for (BlockPos pos : leaves) {
            int relativeY = pos.getY() - minY;
            double percentile = (double) relativeY / (totalHeight > 0 ? totalHeight : 1);

            if (percentile > 0.7) { // HAUT
                context.setBlock(pos, isFlowering
                        ? VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.defaultBlockState()
                        : VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.defaultBlockState());
            } else if (percentile > 0.3) { // MILIEU
                context.setBlock(pos, isFlowering
                        ? VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.defaultBlockState()
                        : VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.defaultBlockState());
            } else { // BAS
                context.setBlock(pos, isFlowering
                        ? VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.defaultBlockState()
                        : VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.defaultBlockState());
            }
        }
    }
}
