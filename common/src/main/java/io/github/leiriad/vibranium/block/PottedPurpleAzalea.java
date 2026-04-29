package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.world.level.block.state.BlockState;

public class PottedPurpleAzalea extends FlowerPotBlock {
    //PROPERTIES
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return  BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_AZALEA)
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 2);
    }
    public static final MapCodec<FlowerPotBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("potted").forGetter(FlowerPotBlock::getPotted),
                            propertiesCodec())
                    .apply(instance, PottedPurpleAzalea::new)
    );
    @Override
    public MapCodec<FlowerPotBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public PottedPurpleAzalea(Block content, Properties properties) {
        super(content, properties);
    }

}
