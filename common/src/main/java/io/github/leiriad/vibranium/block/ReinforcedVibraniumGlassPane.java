package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.material.MapColor;

public class ReinforcedVibraniumGlassPane extends IronBarsBlock {
    //PROPERTIES
    public static final MapCodec<IronBarsBlock> CODEC = simpleCodec(ReinforcedVibraniumGlassPane::new);
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.GLASS_PANE)
                .strength(9f,200f)
                .mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops();
    }
    @Override
    public MapCodec<? extends IronBarsBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public ReinforcedVibraniumGlassPane(Properties properties) {
        super(properties);
    }
}
