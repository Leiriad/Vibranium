package io.github.leiriad.vibranium.block;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.Block;

public class BlueGlowBerries extends BlockItem {
    public static Item.Properties getProperties() {
        return new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .saturationModifier(0.1f)
                        .alwaysEdible()
                        .build())

                .component(DataComponents.CONSUMABLE, Consumables.defaultFood()
                        .consumeSeconds(0.8f)
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 200, 0), 1.0f))
                        .build());
    }
    public BlueGlowBerries(Block block, Properties properties) {super(block, properties);}
}
