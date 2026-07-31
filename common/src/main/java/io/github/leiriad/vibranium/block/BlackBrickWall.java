package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.material.MapColor;


public class BlackBrickWall extends WallBlock {

    //PROPERTIES
    public static final MapCodec<WallBlock> CODEC = simpleCodec(BlackBrickWall::new);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.BRICK_WALL).mapColor(MapColor.COLOR_BLACK);
    }
    @Override
    public MapCodec<WallBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public BlackBrickWall(Properties properties) {
        super(properties);
    }
}
