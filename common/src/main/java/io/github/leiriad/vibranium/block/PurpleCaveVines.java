package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

import static io.github.leiriad.vibranium.utils.VibraniumBlockActions.getBlock;

public class PurpleCaveVines extends CaveVinesBlock {
    //PROPERTIES
    private static String BLOCKPARENT = "purple_cave_vines_plant";
    ///Copies vanilla Cave Vines properties
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return  BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES);
    }
    public static final MapCodec<CaveVinesBlock> CODEC = simpleCodec(PurpleCaveVines::new);
    @Override
    public MapCodec<CaveVinesBlock> codec() {
        return CODEC;
    }

    //CONSTRUTOR
    public PurpleCaveVines(Properties properties) {
        super(properties);
    }

    //ACTIONS
    ///Tells the vine which body to use for its growth
    @Override
    protected Block getBodyBlock() {
        return getBlock(BLOCKPARENT, Blocks.CAVE_VINES_PLANT);
    }
    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }


}
