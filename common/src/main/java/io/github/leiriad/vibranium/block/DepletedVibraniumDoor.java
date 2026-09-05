package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.sound.VibraniumSoundType;
import io.github.leiriad.vibranium.utils.VibraniumBlockSetType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class DepletedVibraniumDoor extends DoorBlock {
    //PROPERTIES
    public static final MapCodec<DepletedVibraniumDoor> CODEC = simpleCodec(DepletedVibraniumDoor::new);
    public static Properties getProperties (Properties settings){
    return Properties.ofFullCopy(Blocks.IRON_DOOR).mapColor(MapColor.COLOR_BLACK)
                .sound(VibraniumSoundType.DEPLETED_VIBRANIUM_BLOCK)
                .strength(36F, 798F)
                .requiresCorrectToolForDrops();
    }
    @Override
    public MapCodec<? extends DoorBlock> codec() {
        return CODEC;
    }

    //CONSTRUCTOR
    public DepletedVibraniumDoor(Properties properties) {
        super(VibraniumBlockSetType.VIBRANIUM, properties);
    }

    //METHODS
    //Depleted vibranium objects absorb sound
    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
    }
}
