package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.block.BaseElectricWireBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricWireItem extends StandingAndWallBlockItem {

    private final DyeColor color;

    public ElectricWireItem(Block floorBlock, Block wallBlock, DyeColor color, Properties properties) {
        super(floorBlock, wallBlock, Direction.DOWN, properties);
        this.color = color;
    }

    public DyeColor getColor() {
        return this.color;
    }
    @Override
    public BlockState getPlacementState(BlockPlaceContext context) {
        // Let block calculate its placement as usual
        BlockState state = super.getPlacementState(context);

        // Inject color
        if (state != null && state.hasProperty(BaseElectricWireBlock.COLOR)) {
            return state.setValue(BaseElectricWireBlock.COLOR, this.color);
        }

        return state;
    }
}
