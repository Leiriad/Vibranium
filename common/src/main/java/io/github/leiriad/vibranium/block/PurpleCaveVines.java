package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.BlockHitResult;

import static io.github.leiriad.vibranium.init.VibraniumBlocks.PURPLE_CAVE_VINES_PLANT;

public class PurpleCaveVines extends CaveVinesBlock {
    //PROPERTIES
    ///Copies vanilla Cave Vines properties
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return  BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES);
    }
    public static final MapCodec<CaveVinesBlock> CODEC = simpleCodec(PurpleCaveVines::new);
    @Override
    public MapCodec<CaveVinesBlock> codec() {
        return CODEC;
    }

    //CONSTRUTOR
    public PurpleCaveVines(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BERRIES, false));
    }

    //ACTIONS
    ///Tells the vine which body to use for its growth
    @Override
    protected Block getBodyBlock() {
        return PURPLE_CAVE_VINES_PLANT.get();
    }
    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
        return new ItemStack(VibraniumBlocks.BLUE_GLOW_BERRIES.get());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {

        if (state.getValue(BERRIES)) {
            // On joue le son
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
