package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.world.level.block.state.BlockState;

public class PottedFloweringPurpleAzalea extends FlowerPotBlock {
    //PROPERTIES
    public static Properties getProperties(Properties settings){
        return  Properties.ofFullCopy(Blocks.POTTED_FLOWERING_AZALEA)
                .emissiveRendering((state, level, pos) -> {return true;})
                .hasPostProcess((state, level, pos) -> {return true;})
                .lightLevel((state) -> 2);
    }
    public static final MapCodec<FlowerPotBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("potted").forGetter(FlowerPotBlock::getPotted),
                            propertiesCodec())
                    .apply(instance, PottedFloweringPurpleAzalea::new)
    );
    @Override
    public MapCodec<FlowerPotBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public PottedFloweringPurpleAzalea(Block content, Properties properties) {
        super(content, properties);
    }

}
