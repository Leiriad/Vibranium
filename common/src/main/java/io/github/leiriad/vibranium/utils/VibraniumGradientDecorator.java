package io.github.leiriad.vibranium.utils;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumTreeDecorators;

import java.util.Comparator;
import java.util.List;

///Gradient tree making tool
public class VibraniumGradientDecorator extends TreeDecorator {

    public VibraniumGradientDecorator() {
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return VibraniumTreeDecorators.GRADIENT_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        List<BlockPos> leaves = context.leaves();
        if (leaves.isEmpty()) return;

        //Get leaves
        Block FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE = VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get();
        Block PURPLE_AZALEA_LEAVES_DARK_BLUE = VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get();
        Block FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET = VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get();
        Block PURPLE_AZALEA_LEAVES_VIOLET = VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get();
        Block FLOWERING_PURPLE_AZALEA_LEAVES_CYAN = VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get();
        Block PURPLE_AZALEA_LEAVES_CYAN = VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get();

        //Getting higher and lower point of the tree leaves
        int maxY = leaves.stream().max(Comparator.comparingInt(BlockPos::getY)).get().getY();
        int minY = leaves.stream().min(Comparator.comparingInt(BlockPos::getY)).get().getY();
        int totalHeight = maxY - minY;

        //Find the first leaf to figure out if it's a flowering tree
        boolean isFlowering = context.level().isStateAtPosition(
                leaves.get(0),
                (state) -> state != null && state.is(FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE)
        );

        //Replace the placeholder leaves by the real ones
        for (BlockPos pos : leaves) {
            int relativeY = pos.getY() - minY;
            double percentile = (double) relativeY / (totalHeight > 0 ? totalHeight : 1);

            if (percentile > 0.7) { // HAUT
                context.setBlock(pos, isFlowering
                        ? FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.defaultBlockState()
                        : PURPLE_AZALEA_LEAVES_VIOLET.defaultBlockState());
            } else if (percentile > 0.3) { // MILIEU
                context.setBlock(pos, isFlowering
                        ? FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.defaultBlockState()
                        : PURPLE_AZALEA_LEAVES_DARK_BLUE.defaultBlockState());
            } else { // BAS
                context.setBlock(pos, isFlowering
                        ? FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.defaultBlockState()
                        : PURPLE_AZALEA_LEAVES_CYAN.defaultBlockState());
            }
        }
    }
}
