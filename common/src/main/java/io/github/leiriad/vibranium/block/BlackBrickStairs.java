package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.material.MapColor;


public class BlackBrickStairs extends StairBlock {

    //PROPERTIES
    public static final MapCodec<StairBlock> CODEC = simpleCodec(BlackBrickStairs::new);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.BRICK_STAIRS).mapColor(MapColor.COLOR_BLACK);
    }
    @Override
    public MapCodec<? extends StairBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public BlackBrickStairs(Properties properties) {
        super(VibraniumBlocks.BLACK_BRICKS.get().defaultBlockState(), properties);
    }
}
