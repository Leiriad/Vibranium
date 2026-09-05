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

public class ReactorCoreItem extends BlockItem {
    public static Item.Properties getProperties() {
        return new Item.Properties();
    }
    public ReactorCoreItem(Block block, Properties properties) {
        super(block, properties);
    }
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".reactor_core.function")
                .withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".reactor_core.multiblock_req")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".reactor_core.multiblock_req_2")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".reactor_core.multiblock_req_3")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".reactor_core.warning")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}