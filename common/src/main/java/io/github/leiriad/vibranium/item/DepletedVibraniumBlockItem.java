package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;


import java.util.function.Consumer;

public class DepletedVibraniumBlockItem extends BlockItem {
    public static Properties getProperties() {
        return new Properties();
    }
    public DepletedVibraniumBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        // Acoustic insulation description
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".depleted_block.sound_info")
                .withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }

}