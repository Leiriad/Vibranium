package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import io.github.leiriad.vibranium.utils.VibraniumToolMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class VibraniumSpear extends Item {

    // Unique attribute modifier IDs
    private static final Identifier SPEAR_DAMAGE_ID = Identifier.fromNamespaceAndPath("vibranium", "spear_damage");
    private static final Identifier SPEAR_SPEED_ID = Identifier.fromNamespaceAndPath("vibranium", "spear_speed");
    private static final Identifier SPEAR_REACH_ID = Identifier.fromNamespaceAndPath("vibranium", "spear_reach");

    public VibraniumSpear(Properties properties) {
        super(properties);
    }

    public static Item.Properties getProperties(Item.Properties settings) {
        Item.Properties props = settings.spear(
                VibraniumToolMaterial.VIBRANIUM,
                0.625F, // f: Attack / thrust speed
                1.5F,   // g: Charge damage multiplier
                0.1F,   // h: Very short MINIMUM charge duration (maximum responsiveness)

                // ENGAGEMENT Phase (Stability) - Increased!
                3.0F,   // i: Threshold 1 - The spear remains stable for 3.0 seconds (instead of 0.5s)
                1.0F,   // j: Value multiplier 1

                // TRANSITION Phase
                5.0F,   // k: Threshold 2 - Shaking delayed to 5.0 seconds
                1.0F,   // l: Value multiplier 2

                // RELATIVE Phase
                5.0F,   // m: Threshold 3
                1.0F    // n: Value multiplier 3
        );
        // Range (+2 blocks compared to vanilla spear)
        props.component(DataComponents.ATTACK_RANGE, new AttackRange(2.0F, 5.0F, 2.0F, 7.0F, 0.125F, 0.5F));

        props.attributes(
                ItemAttributeModifiers.builder()
                        .add(
                                Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 7.5F, AttributeModifier.Operation.ADD_VALUE), // Enhanced damage
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                Attributes.ATTACK_SPEED,
                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.5F, AttributeModifier.Operation.ADD_VALUE), // Enhanced speed (speed = 1.5 - 4.0)
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                Attributes.ENTITY_INTERACTION_RANGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 2.0F, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .build()
        );

        return props;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

        if (charge > 10.0F) { // Requires a minimum charge threshold
            if (!level.isClientSide()) {
                double radius = 3.0 + (charge / 20.0); // Effect radius based on charge
                float shockwaveDamage = charge * 0.15F;  // Energy-based damage

                // Find all surrounding entities
                List<LivingEntity> targets = level.getEntitiesOfClass(
                        LivingEntity.class,
                        player.getBoundingBox().inflate(radius),
                        e -> e != player && e.isAlive()
                );

                for (LivingEntity target : targets) {
                    // Apply repulsive knockback
                    Vec3 direction = target.position().subtract(player.position()).normalize();
                    target.knockback(1.5F + (charge / 50.0F), -direction.x, -direction.z);

                    // Deal shockwave damage
                    target.hurt(level.damageSources().playerAttack(player), shockwaveDamage);
                }

                // Visual and sound effects
                ServerLevel serverLevel = (ServerLevel) level;
                serverLevel.sendParticles(
                        ParticleTypes.SONIC_BOOM,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        1, 0.0, 0.0, 0.0, 0.0
                );
                level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.2F);

                // Reset charge
                stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            }

            player.getCooldowns().addCooldown(stack, 40); // 2-second cooldown after release
            return InteractionResult.SUCCESS;
        }

        return super.use(level, player, hand);
    }
    @Override
    public boolean isBarVisible(ItemStack stack) {
        // La barre apparaît dès que la lance a au moins un peu de charge
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        return charge > 0.0F;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        // 13 est la largeur maximale en pixels de la barre dans l'inventaire Minecraft
        return Math.round((Math.min(charge, 100.0F) / 100.0F) * 13.0F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Couleur violette/cyan style Vibranium (Format RGB en hexadécimal)
        return 0x9933FF; // Violet Vibranium
    }
    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        if (entity instanceof Player player) {
            // Vérification directe sur l'item tenu en main principale (contourne la limitation d'equipmentSlot)
            boolean isHoldingInMainHand = player.getMainHandItem() == itemStack;

            if (isHoldingInMainHand) {
                float charge = itemStack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

                if (charge > 10.0F) {
                    float particleChance = (charge / 100.0F) * 0.3F;

                    if (serverLevel.random.nextFloat() < particleChance) {
                        serverLevel.sendParticles(
                                ParticleTypes.REVERSE_PORTAL,
                                player.getX() + (serverLevel.random.nextDouble() - 0.5) * 0.8,
                                player.getY() + serverLevel.random.nextDouble() * 1.8,
                                player.getZ() + (serverLevel.random.nextDouble() - 0.5) * 0.8,
                                1, 0.0, 0.02, 0.0, 0.0
                        );
                    }
                }
            }
        }
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

        if (charge > 0.0F) {
            consumer.accept(
                    Component.literal("Kinetic Charge: " + (int) charge + "%")
                            .withStyle(ChatFormatting.LIGHT_PURPLE)
            );
        } else {
            consumer.accept(
                    Component.literal("Kinetic Charge: 0%")
                            .withStyle(ChatFormatting.GRAY)
            );
        }

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}