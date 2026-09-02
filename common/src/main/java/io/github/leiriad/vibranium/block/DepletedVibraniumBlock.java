package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.sound.VibraniumSoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class DepletedVibraniumBlock extends Block{
    //PROPERTIES
    public static final MapCodec<Block> CODEC = simpleCodec(BlackBricks::new);
    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_BLACK)
                .sound(VibraniumSoundType.DEPLETED_VIBRANIUM_BLOCK)
                .strength(60.0F, 1200.0F)
                .requiresCorrectToolForDrops();
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public DepletedVibraniumBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }


    //Depleted vibranium absorb sound
    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
    }

}
