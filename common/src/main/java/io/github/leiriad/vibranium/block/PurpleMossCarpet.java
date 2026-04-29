package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.material.MapColor;

public class PurpleMossCarpet extends CarpetBlock {
    //PROPERTIES
    public static final MapCodec<CarpetBlock> CODEC = simpleCodec(PurpleMossCarpet::new);
    @Override
    public MapCodec<? extends CarpetBlock> codec() {
        return CODEC;
    }
    ///Copying all vanilla moss_carpet properties
    public static Properties getProperties(Properties settings){
        return Properties.ofFullCopy(Blocks.MOSS_CARPET).mapColor(MapColor.COLOR_PURPLE);
    }

    //CONSTRUCTOR
    public PurpleMossCarpet(Properties properties) {
        super(properties);
    }
}
