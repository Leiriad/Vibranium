package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.material.MapColor;

public class VibraniumGlassPane extends IronBarsBlock {
    //PROPERTIES
    public static final MapCodec<IronBarsBlock> CODEC = simpleCodec(VibraniumGlassPane::new);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.GLASS_PANE).mapColor(MapColor.COLOR_PINK);
    }
    @Override
    public MapCodec<? extends IronBarsBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public VibraniumGlassPane(Properties properties) {
        super(properties);
    }
}
