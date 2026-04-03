package vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import vibranium.init.VibraniumBlocks;
import vibranium.utils.VibraniumBlockActions;

public class VibraniumFarmland extends FarmBlock {

    public VibraniumFarmland(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        //Vibranium_Grass_Block make plants grow faster with a risk of destruction
        VibraniumBlockActions.fertilizes(world, random, pos);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        //Turns back to dirt if block over it
        if (direction == Direction.UP && !state.canSurvive(world, pos)) {
            return VibraniumBlocks.VIBRANIUM_DIRT.defaultBlockState();
        }
        return super.updateShape(state, world, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, double d) {
        //Turns back to dirt if trampled on
        if (world instanceof ServerLevel serverLevel
                && world.random.nextFloat() < d - 0.5
                && entity instanceof LivingEntity
                && (entity instanceof Player || serverLevel.getGameRules().get(GameRules.MOB_GRIEFING))
                && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
            VibraniumBlockActions.turnToVibraniumDirt(entity, state, world, pos);
        }
        super.fallOn(world, state, pos, entity, d);
    }
}

