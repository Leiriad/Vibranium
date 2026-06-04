package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;


public class ReinforcedVibraniumGlass extends Block {

    //PROPERTIES
    public static final MapCodec<Block> CODEC = simpleCodec(ReinforcedVibraniumGlass::new);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.GLASS)
                .strength(1.5f,6.0f)
                .mapColor(MapColor.COLOR_BLACK);
    }
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public ReinforcedVibraniumGlass(Properties properties) {
        super(properties);
    }
}
