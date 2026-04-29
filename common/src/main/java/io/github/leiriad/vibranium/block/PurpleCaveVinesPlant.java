package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

import java.util.HashMap;

import static io.github.leiriad.vibranium.utils.VibraniumBlockActions.getBlock;

public class PurpleCaveVinesPlant extends CaveVinesPlantBlock {
    //PROPERTIES
    private static String HEADBLOCK = "purple_cave_vines";
    ///Copies vanilla Cave Vines properties
    public static Properties getProperties(Properties settings){
        return  Properties.ofFullCopy(Blocks.CAVE_VINES_PLANT);
    }
    public static final MapCodec<CaveVinesPlantBlock> CODEC = simpleCodec(PurpleCaveVinesPlant::new);
    @Override
    public MapCodec<CaveVinesPlantBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public PurpleCaveVinesPlant(Properties properties) {
        super(properties);
    }

    //ACTIONS
    ///Tells the body(plant) which head to follow
    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) getBlock(HEADBLOCK,Blocks.CAVE_VINES);
    }
}
