package io.github.leiriad.vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.utils.VibraniumBlockActions;

import static io.github.leiriad.vibranium.block.VibraniumCommonDirtProperties.baseVibraniumDirtSettings;

public class VibraniumFarmland extends FarmBlock {
    ///Vibranium_farmland constructor
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return baseVibraniumDirtSettings();
    }

    ///Tells us if the vibranium_farmland block should remain as is. If it returns false, block turns back to dirt
    private static boolean shouldMaintainFarmland(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos.above()).is(BlockTags.MAINTAINS_FARMLAND);
    }

    ///Gets vibranium_farmland block's hydration status
    private static boolean isNearWater(LevelReader levelReader, BlockPos blockPos) {
        for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.offset(-4, 0, -4), blockPos.offset(4, 1, 4))) {
            if (levelReader.getFluidState(blockPos2).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
    public VibraniumFarmland(Properties properties) {
        super(properties);
    }

    ///Defines the vibranium_farmland block behavior
    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        //Vibranium_Farmland turns back to vibranium_dirt when dry
        int moisture = state.getValue(MOISTURE);

        if (!isNearWater(world, pos) && !world.isRainingAt(pos.above())) {
            if (moisture > 0) {
                world.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
            } else if (!shouldMaintainFarmland(world, pos)) {
                turnToDirt(null, state, world, pos);
            }
        } else if (moisture < 7) {
            world.setBlock(pos, state.setValue(MOISTURE, 7), 2);
        }
        //Vibranium_Farmland make plants grow faster with a risk of destruction
        VibraniumBlockActions.fertilizes(world, random, pos);
    }

    ///Prevents block to turn into vanilla dirt
    public static void turnToDirt(@Nullable Entity entity, BlockState blockState, Level level, BlockPos blockPos) {
        BlockState blockState2 = pushEntitiesUp(blockState, VibraniumBlocks.VIBRANIUM_DIRT.defaultBlockState(), level, blockPos);
        level.setBlockAndUpdate(blockPos, blockState2);
        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, blockState2));
    }

    ///To turn block into dirt if dry or under another block
    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {

        if (direction == Direction.UP && !state.canSurvive(world, pos)) {
            return VibraniumBlocks.VIBRANIUM_DIRT.defaultBlockState();
        }
        return super.updateShape(state, world, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    ///To turn block back to vibranium_dirt if its trampled on
    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, double d) {
            if (world instanceof ServerLevel serverLevel
                && world.random.nextFloat() < d - 0.5
                && entity instanceof LivingEntity
                && (entity instanceof Player || serverLevel.getGameRules().get(GameRules.MOB_GRIEFING))
                && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
            VibraniumBlockActions.turnToVibraniumDirt(entity, state, world, pos);
        }
    }
}

