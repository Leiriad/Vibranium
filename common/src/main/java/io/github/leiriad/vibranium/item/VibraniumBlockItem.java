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

public class VibraniumBlockItem extends BlockItem {
    public static Item.Properties getProperties() {
        return new Item.Properties();
    }
    public VibraniumBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        // How to charge the block when placed
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".block.charge_info")
                .withStyle(ChatFormatting.GRAY));

        // How to trigger the kinetic release (Right-click / Redstone)
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".block.release_info")
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        // Universal looting effect description (Crops, Ores, Dirt, Stone)
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".block.shockwave_effect")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }

}