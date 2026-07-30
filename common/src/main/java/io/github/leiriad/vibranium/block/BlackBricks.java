package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;


public class BlackBricks extends Block {

    //PROPERTIES
    public static final MapCodec<Block> CODEC = simpleCodec(BlackBricks::new);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.COLOR_BLACK);
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public BlackBricks(Properties properties) {
        super(properties);
    }
}
