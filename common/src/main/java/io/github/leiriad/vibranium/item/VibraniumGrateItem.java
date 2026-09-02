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

// Code comments are kept in English as per project conventions.
public class VibraniumGrateItem extends BlockItem {
    public static Properties getProperties() {
        return new Properties();
    }
    public VibraniumGrateItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".grate.info")
                .withStyle(ChatFormatting.GRAY));

        // How to trigger the kinetic release (walked_on powered grate)
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".grate.repulsion")
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }

}