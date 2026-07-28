package io.github.leiriad.vibranium.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;

import java.util.List;

public class BlueBerrySpirit extends PotionItem {
    public static Item.Properties getProperties() {
        return new Item.Properties()
                .stacksTo(16) // Max stack of 16 like potions/bottles
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .saturationModifier(0.2F)
                        .alwaysEdible()
                        .build())
                .component(DataComponents.CONSUMABLE, Consumables.defaultDrink()
                        .consumeSeconds(1.6f) // Drink duration
                        // Alcohol effect: Nausea / Confusion
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SLOWNESS, 300, 0), 1.0F))
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 300, 0, false, false, false), 1.0F))
                        .build());

    }

    public BlueBerrySpirit(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId());
    }
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK; // Playing drinking animation
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack resultStack = super.finishUsingItem(stack, level, user);

        // Returns empty glass bottle to the player
        if (user instanceof Player player && !player.getAbilities().instabuild) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }

        return resultStack;
    }
}
