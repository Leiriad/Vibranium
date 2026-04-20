package io.github.leiriad.vibranium.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;

public class PurpleMossCarpet extends CarpetBlock {
    ///Copying all vanilla moss_carpet properties
    public static Properties getProperties(Properties settings){
        return Properties.ofFullCopy(Blocks.MOSS_CARPET);
    }
    ///purple_moss_carpet constructor
    public PurpleMossCarpet(Properties properties) {
        super(properties);
    }
}
