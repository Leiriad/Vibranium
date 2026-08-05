package io.github.leiriad.vibranium.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;

public class AncestralNectar extends Item {
    public static Item.Properties getProperties() {
        int effectDuration=168000;
        return new Item.Properties()
                // Standard nutrition and saturation values
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationModifier(0.8F)
                        .alwaysEdible()
                        .build())
                // Consumable component defining drink timing and status effects
                .component(DataComponents.CONSUMABLE, Consumables.defaultFood()
                        .consumeSeconds(1.6f) // Drinking duration (slightly faster than standard food)

                        // Physical Buffs: Strength II & Movement Speed II
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.STRENGTH, effectDuration, 1), 1.0F))
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, effectDuration, 1), 1.0F))

                        // Vibranium Resilience: Resistance I & Absorption II (4 bonus golden hearts)
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.RESISTANCE, effectDuration, 0), 1.0F))
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, effectDuration, 1), 1.0F))

                        // Enhanced Perception: Jump Boost II & Night Vision
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, effectDuration, 1), 1.0F))
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, effectDuration, 0), 1.0F))
                        .build());
    }

    //CONSTRUCTOR
    public AncestralNectar(Properties properties) {super(properties);}

    //METHODS
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack resultStack = super.finishUsingItem(stack, level, user);

        if (user instanceof Player player && !player.getAbilities().instabuild) {
            if (resultStack.isEmpty()) {
                return new ItemStack(Items.BOWL);
            }
            player.getInventory().add(new ItemStack(Items.BOWL));
        }

        return resultStack;
    }
}
