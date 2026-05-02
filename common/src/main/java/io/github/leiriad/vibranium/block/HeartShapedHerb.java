package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

import static io.github.leiriad.vibranium.init.VibraniumBlocks.VIBRANIUM_GRASS_BLOCK;

public class HeartShapedHerb extends BushBlock {
    //PROPERTIES
    public static final IntegerProperty AGE = IntegerProperty.create("age",0,2);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<BushBlock> CODEC = simpleCodec(HeartShapedHerb::new);
    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.RED_TULIP).randomTicks()
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 2);
    }
    @Override
    public MapCodec<BushBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    ///The herb as 3 growth stages sprout, bud, flower and spawns in random directions
    public HeartShapedHerb(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(AGE,0)
                .setValue(FACING,Direction.NORTH));
    }

    //ACTIONS
    ///Allows the herb to grow with time
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        //Depending on the plant's age it will grow with time even if Vibranium grass blocks fertilizes more, it grows slowly
        if(level.getBlockState(pos.below()).is(VIBRANIUM_GRASS_BLOCK)){
            if (age < 2 && level.getMaxLocalRawBrightness(pos) >= 9) {
                if (random.nextInt(40) == 0) {
                    level.setBlock(pos, state.setValue(AGE, age + 1), 2);
                }
            }
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        super.createBlockStateDefinition(builder);
        builder.add(AGE,FACING);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos groundPos = pos.below();
        BlockState groundState = level.getBlockState(groundPos);

        return groundState.is(VIBRANIUM_GRASS_BLOCK);
    }
    ///Direction when the player plants the herb
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // the plant will face the direction opposite to the player like an oven
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

}
