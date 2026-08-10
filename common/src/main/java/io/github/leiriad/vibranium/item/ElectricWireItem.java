package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;

public class ElectricWireItem extends StandingAndWallBlockItem {

    public ElectricWireItem(Properties properties) {
        super(VibraniumBlocks.ELECTRIC_WIRE.get(), VibraniumBlocks.ELECTRIC_WIRE_WALL.get(), Direction.DOWN, properties);
    }
}
