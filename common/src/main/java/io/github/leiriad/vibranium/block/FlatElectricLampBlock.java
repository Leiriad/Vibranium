package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FlatElectricLampBlock extends BaseElectricLampBlock {
    public static final MapCodec<BaseElectricLampBlock> CODEC = simpleCodec(FlatElectricLampBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static BlockBehaviour.Properties getProperties (BlockBehaviour.Properties settings){
        return BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_LAMP);
    }
    @Override
    public MapCodec<BaseElectricLampBlock> codec() {
        return CODEC;
    }

    public FlatElectricLampBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }
}
