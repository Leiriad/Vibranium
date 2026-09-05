package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class ReactorPumpItem extends BlockItem {
    public static Item.Properties getProperties() {
        return new Item.Properties();
    }
    public ReactorPumpItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".reactor_pump.function")
                .withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}