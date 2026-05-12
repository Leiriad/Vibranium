package io.github.leiriad.vibranium.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class HeartShapedHerb extends Item {
    public static Item.Properties getProperties() {
        return new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .saturationModifier(0.1f)
                        .alwaysEdible()
                        .build())

                .component(DataComponents.CONSUMABLE, Consumables.defaultFood()
                        .consumeSeconds(0.8f)
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 400, 0), 1.0f))
                        .build());
    }
    public HeartShapedHerb(Properties properties) {super(properties);}
}
