package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.sound.VibraniumSoundType;
import io.github.leiriad.vibranium.utils.VibraniumBlockSetType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DepletedVibraniumTrapdoor extends TrapDoorBlock{
    //PROPERTIES
    public static final MapCodec<DepletedVibraniumTrapdoor> CODEC = simpleCodec(DepletedVibraniumTrapdoor::new);
    @Override
    public MapCodec<? extends TrapDoorBlock> codec() {
        return CODEC;
    }
    public static Properties getProperties(Properties settings) {
        return Properties.ofFullCopy(Blocks.IRON_TRAPDOOR).mapColor(MapColor.COLOR_BLUE)
                .sound(VibraniumSoundType.DEPLETED_VIBRANIUM_GRATE)
                .strength(24F, 532F)
                .requiresCorrectToolForDrops();
    }

    //CONSTRUCTOR
    public DepletedVibraniumTrapdoor(Properties properties) {
        super(VibraniumBlockSetType.VIBRANIUM, properties);
    }

    //METHODS
    //Depleted vibranium objects absorb sound
    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
    }
}