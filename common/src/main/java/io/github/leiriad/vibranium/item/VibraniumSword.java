package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import io.github.leiriad.vibranium.utils.VibraniumToolMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Consumer;

public class VibraniumSword extends Item {

    public VibraniumSword(Properties properties) {
        super(properties);
    }

    public static Item.Properties getProperties(Item.Properties settings) {
        Item.Properties props = settings
                .enchantable(22);

        VibraniumToolMaterial.VIBRANIUM.applySwordProperties(props,6.0F, -2.2F);

        props.attributes(
                ItemAttributeModifiers.builder()
                        .add(
                                Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 6.0F, AttributeModifier.Operation.ADD_VALUE), // Base damage 9 (3 + 6)
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                Attributes.ATTACK_SPEED,
                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.2F, AttributeModifier.Operation.ADD_VALUE), // Speed 1.8
                                EquipmentSlotGroup.MAINHAND
                        )
                        // Lore: Vibranium absorbs physical feedback, making the wielder immune to knockback
                        .add(
                                Attributes.KNOCKBACK_RESISTANCE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 0.25F, AttributeModifier.Operation.ADD_VALUE), // +25% knockback resistance
                                EquipmentSlotGroup.MAINHAND
                        )
                        .build()
        );

        return props;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // Start blocking / parrying stance (like shield or trident channel)
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK; // Play blocking animation in third person
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000; // Can hold guard for up to 1 hour (standard shield behavior)
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.level() instanceof ServerLevel serverLevel && attacker instanceof Player player) {
            float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

            if (charge >= 10.0F) { // Minimal charge required to trigger discharge
                float radius = 2.5F + (charge / 25.0F);  // Shockwave radius
                float force = 1.0F + (charge / 50.0F);   // Knockback force
                float bonusDamage = charge * 0.25F;      // Increased scaling for noticeable damage

                // 1. Trigger the visual and physical shockwave around the struck target
                VibraniumToolActions.spawnShockwave(serverLevel, target.position(), radius, force, player);

                // 2. Deal shockwave area damage to surrounding enemies (excluding the wielder)
                double diameter = radius * 2.0;
                AABB area = AABB.ofSize(target.position(), diameter, diameter, diameter);
                List<LivingEntity> nearbyTargets = serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        area,
                        e -> e != player && e.isAlive()
                );

                for (LivingEntity nearby : nearbyTargets) {
                    // Bypass invulnerability frames briefly for the direct target if needed
                    nearby.invulnerableTime = 0;
                    nearby.hurt(serverLevel.damageSources().playerAttack(player), bonusDamage);
                }

                // 3. Reset charge after releasing energy
                stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            }
        }

        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F) > 0.0F;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        return Math.round((Math.min(charge, 100.0F) / 100.0F) * 13.0F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x9933FF; // Violet
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            TooltipFlag tooltipFlag
    ) {
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        int percentage = (int) charge;

        ChatFormatting color = (percentage > 0) ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY;

        consumer.accept(
                Component.translatable("tooltip.vibranium.weapons.charge", percentage)
                        .withStyle(color)
        );

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}