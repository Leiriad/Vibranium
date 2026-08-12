package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.block.BaseElectricWireBlock;
import io.github.leiriad.vibranium.entity.ElectricWireEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

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
    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            TooltipFlag tooltipFlag
    ) {
        int tranferRate = ElectricWireEntity.maxTransfer;
        consumer.accept(
                Component.translatable("tooltip."+ VibraniumMod.MOD_ID+".wire.tranfer_rate", tranferRate/1000)
                        .withStyle(ChatFormatting.GRAY)
        );

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}
