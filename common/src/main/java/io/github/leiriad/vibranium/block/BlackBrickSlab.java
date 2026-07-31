package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.material.MapColor;


public class BlackBrickSlab extends SlabBlock {

    //PROPERTIES
    public static final MapCodec<SlabBlock> CODEC = simpleCodec(BlackBrickSlab::new);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.BRICK_SLAB).mapColor(MapColor.COLOR_BLACK);
    }
    @Override
    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public BlackBrickSlab(Properties properties) {
        super(properties);
    }
}
