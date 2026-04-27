package io.github.leiriad.vibranium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;


public class PurpleTallGrass extends DoublePlantBlock {
    public static Properties getProperties (Properties settings){
        return Properties.ofFullCopy(Blocks.TALL_GRASS)
                .mapColor(MapColor.COLOR_PURPLE)
                .emissiveRendering((state, level, pos) -> true)
                .hasPostProcess((state, level, pos) -> true)
                .lightLevel((state) -> 1);
    }
    public PurpleTallGrass(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, livingEntity, itemStack);
        //Placing the top block above the main block
        if (!world.isClientSide()) {

            BlockPos upperPos = pos.above();
            world.setBlock(upperPos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 3);
        }

    }
}
