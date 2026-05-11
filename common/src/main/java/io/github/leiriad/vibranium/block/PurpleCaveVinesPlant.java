package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import static io.github.leiriad.vibranium.init.VibraniumBlocks.PURPLE_CAVE_VINES;

public class PurpleCaveVinesPlant extends CaveVinesPlantBlock {
    //PROPERTIES
    ///Copies vanilla Cave Vines properties
    public static Properties getProperties(Properties settings){
        return  Properties.ofFullCopy(Blocks.CAVE_VINES_PLANT);
    }
    public static final MapCodec<CaveVinesPlantBlock> CODEC = simpleCodec(PurpleCaveVinesPlant::new);
    @Override
    public MapCodec<CaveVinesPlantBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public PurpleCaveVinesPlant(Properties properties) {
        super(properties);
    }

    //ACTIONS
    ///Tells the body(plant) which head to follow
    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock)PURPLE_CAVE_VINES.get();
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
        return new ItemStack(VibraniumBlocks.BLUE_GLOW_BERRIES.get());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {

        if (state.getValue(BERRIES)) {

            float pitch = Mth.randomBetween(level.random, 0.8F, 1.2F);
            level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, pitch);


            BlockState newState = state.setValue(BERRIES, false);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));

            if (!level.isClientSide()) {
                Block.popResource(level, pos, new ItemStack(VibraniumBlocks.BLUE_GLOW_BERRIES.get()));
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
