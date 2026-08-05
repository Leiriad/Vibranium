package io.github.leiriad.vibranium.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BlueSoup extends Item {

    public static Item.Properties getProperties() {
        return new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(20)            // Restores full hunger bar (10 haunches)
                        .saturationModifier(1.0F) // Provides maximum saturation
                        .alwaysEdible()           // Consumable even if the hunger bar is already full
                        .build())
                .component(DataComponents.CONSUMABLE, Consumables.defaultFood()
                        .consumeSeconds(1.6f)     // Standard consumption duration (1.6s)
                        .build());
    }

    public BlueSoup(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack resultStack = super.finishUsingItem(stack, level, user);

        if (!level.isClientSide()) {
            // Remove harmful status effects (debuffs) exclusively
            List<MobEffectInstance> activeEffects = new ArrayList<>(user.getActiveEffects());
            for (MobEffectInstance effect : activeEffects) {
                if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                    user.removeEffect(effect.getEffect());
                }
            }
        }

        // Return empty bowl to the inventory
        if (user instanceof Player player && !player.getAbilities().instabuild) {
            if (resultStack.isEmpty()) {
                return new ItemStack(Items.BOWL);
            }
            if (!player.getInventory().add(new ItemStack(Items.BOWL))) {
                player.drop(new ItemStack(Items.BOWL), false);
            }
        }

        return resultStack;
    }
}