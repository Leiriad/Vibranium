package io.github.leiriad.vibranium.event;

import io.github.leiriad.vibranium.config.VibraniumConfigManager;
import io.github.leiriad.vibranium.item.BlackPantherArmorItem;
import io.github.leiriad.vibranium.item.VibraniumPickaxe;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class AbilityHandler {

    /**
     * Centralized execution for abilities triggered via the primary ability keybind.
     */
    public static void handleAbilityTrigger(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();

        //Priority: Main Hand Weapon/Tool Ability
        if (mainHand.getItem() instanceof VibraniumPickaxe) {
            // Logic to toggle 3x3 mode or trigger Sonar
            return;
        }

        //Secondary Priority: Helmet Tactical Vision
        ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmetStack.getItem() instanceof BlackPantherArmorItem) {
            if (triggerHelmetVision(player, helmetStack)) {
                return;
            }
        }

        //Tertiary Priority: Chestplate Kinetic Discharge
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof BlackPantherArmorItem) {
            triggerChestplateKineticRelease(player, chestStack);
        }
    }

    private static boolean triggerHelmetVision(Player player, ItemStack helmetStack) {
        var armorConfig = VibraniumConfigManager.INSTANCE.armor;
        float currentCharge = helmetStack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        float cost = armorConfig.helmetVisionCost;

        if (currentCharge >= cost) {
            helmetStack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - cost);

            double radius = armorConfig.helmetVisionRadius;
            int duration = armorConfig.helmetVisionDurationTicks;
            AABB scanBounds = player.getBoundingBox().inflate(radius);

            player.level().getEntitiesOfClass(LivingEntity.class, scanBounds, target -> target != player)
                    .forEach(target -> target.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, 0, false, false)));
            return true;
        }
        return false;
    }

    private static void triggerChestplateKineticRelease(Player player, ItemStack chestStack) {
        var armorConfig = VibraniumConfigManager.INSTANCE.armor;
        float currentCharge = chestStack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        float cost = armorConfig.chestplateDischargeCost;

        if (currentCharge >= cost && player.level() instanceof ServerLevel serverLevel) {
            //Deduct energy from chestplate
            chestStack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - cost);

            //Execute shockwave pulse via VibraniumToolActions
            float radius = armorConfig.chestplateDischargeRadius;
            float knockbackForce = 1.8F;

            VibraniumToolActions.spawnShockwave(serverLevel, player.position(), radius, knockbackForce, player);
        }
    }
}