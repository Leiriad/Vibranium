package io.github.leiriad.vibranium.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class HotWaterBucket extends Item {

    public HotWaterBucket(Properties properties) {super(properties);}

    public static Item.Properties getProperties() {
        return new Item.Properties()
                .stacksTo(1)
                .craftRemainder(Items.BUCKET)
                .rarity(Rarity.UNCOMMON)
                .component(DataComponents.CONSUMABLE, Consumables.defaultFood()
                        .consumeSeconds(1.2f)
                        .onConsume(new ApplyStatusEffectsConsumeEffect(
                                // Damage
                                new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1), 1.0f))
                        .onConsume(new ApplyStatusEffectsConsumeEffect(
                                // Fire on screen
                                new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0), 1.0f))
                        .build());
    }
}
