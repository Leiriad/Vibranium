package io.github.leiriad.vibranium.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class VibraniumCommonDirtProperties {


    protected static BlockBehaviour.Properties baseVibraniumDirtSettings() {

        return BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)
                .randomTicks();
    }
}
