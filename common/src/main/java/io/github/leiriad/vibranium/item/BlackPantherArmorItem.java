package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.utils.VibraniumArmorMaterials;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Consumer;

public class BlackPantherArmorItem extends Item implements VibraniumAbilityItem{

    private final ArmorType armorType;
    private static final float MAX_CHARGE = 100.0F;

    private static final Identifier HELMET_ARMOR_ID = Identifier.fromNamespaceAndPath("vibranium", "helmet_armor");
    private static final Identifier HELMET_TOUGHNESS_ID = Identifier.fromNamespaceAndPath("vibranium", "helmet_toughness");
    private static final Identifier HELMET_KNOCKBACK_ID = Identifier.fromNamespaceAndPath("vibranium", "helmet_knockback");

    private static final Identifier CHESTPLATE_ARMOR_ID = Identifier.fromNamespaceAndPath("vibranium", "chestplate_armor");
    private static final Identifier CHESTPLATE_TOUGHNESS_ID = Identifier.fromNamespaceAndPath("vibranium", "chestplate_toughness");
    private static final Identifier CHESTPLATE_KNOCKBACK_ID = Identifier.fromNamespaceAndPath("vibranium", "chestplate_knockback");

    private static final Identifier LEGGINGS_ARMOR_ID = Identifier.fromNamespaceAndPath("vibranium", "leggings_armor");
    private static final Identifier LEGGINGS_TOUGHNESS_ID = Identifier.fromNamespaceAndPath("vibranium", "leggings_toughness");
    private static final Identifier LEGGINGS_KNOCKBACK_ID = Identifier.fromNamespaceAndPath("vibranium", "leggings_knockback");
    private static final Identifier LEGGINGS_SPEED_ID = Identifier.fromNamespaceAndPath("vibranium", "leggings_speed");

    private static final Identifier BOOTS_ARMOR_ID = Identifier.fromNamespaceAndPath("vibranium", "boots_armor");
    private static final Identifier BOOTS_TOUGHNESS_ID = Identifier.fromNamespaceAndPath("vibranium", "boots_toughness");
    private static final Identifier BOOTS_KNOCKBACK_ID = Identifier.fromNamespaceAndPath("vibranium", "boots_knockback");

    public BlackPantherArmorItem(ArmorType armorType, Item.Properties properties) {
        super(properties.humanoidArmor(VibraniumArmorMaterials.BLACK_PANTHER, armorType));
        this.armorType = armorType;
    }

    /**
     * Properties configuration for Helmet.
     */
    public static Item.Properties getHelmetProperties(Item.Properties settings) {
        return settings
                .durability(450)
                .enchantable(20)
                .attributes(
                        ItemAttributeModifiers.builder()
                                .add(Attributes.ARMOR, new AttributeModifier(HELMET_ARMOR_ID, 4.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
                                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(HELMET_TOUGHNESS_ID, 3.5F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
                                .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(HELMET_KNOCKBACK_ID, 0.25F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
                                .build()
                );
    }

    /**
     * Properties configuration for Chestplate.
     */
    public static Item.Properties getChestplateProperties(Item.Properties settings) {
        return settings
                .durability(650)
                .enchantable(20)
                .attributes(
                        ItemAttributeModifiers.builder()
                                .add(Attributes.ARMOR, new AttributeModifier(CHESTPLATE_ARMOR_ID, 9.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
                                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(CHESTPLATE_TOUGHNESS_ID, 3.5F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
                                .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(CHESTPLATE_KNOCKBACK_ID, 0.25F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
                                .build()
                );
    }

    /**
     * Properties configuration for Leggings.
     */
    public static Item.Properties getLeggingsProperties(Item.Properties settings) {
        return settings
                .durability(600)
                .enchantable(20)
                .attributes(
                        ItemAttributeModifiers.builder()
                                .add(Attributes.ARMOR, new AttributeModifier(LEGGINGS_ARMOR_ID, 7.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS)
                                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(LEGGINGS_TOUGHNESS_ID, 3.5F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS)
                                .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(LEGGINGS_KNOCKBACK_ID, 0.25F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS)
                                .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(LEGGINGS_SPEED_ID, 0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.LEGS)
                                .build()
                );
    }

    /**
     * Properties configuration for Boots.
     */
    public static Item.Properties getBootsProperties(Item.Properties settings) {
        return settings
                .durability(400)
                .enchantable(20)
                .attributes(
                        ItemAttributeModifiers.builder()
                                .add(Attributes.ARMOR, new AttributeModifier(BOOTS_ARMOR_ID, 4.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
                                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(BOOTS_TOUGHNESS_ID, 3.5F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
                                .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(BOOTS_KNOCKBACK_ID, 0.25F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
                                .build()
                );
    }
    //COMMON METHODS
    /**
     * Triggers active abilities based on the equipped armor piece when the ability key is pressed.
     */
    @Override
    public void onAbilityKeyPressed(Player player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        switch (this.armorType) {
            case HELMET -> triggerTacticalScan(serverLevel, player);
            case CHESTPLATE -> {
            }
            case LEGGINGS, BOOTS -> {
            }
        }
    }

    /**
     * Highlights nearby living entities through walls for a short duration.
     */
    private void triggerTacticalScan(ServerLevel level, Player player) {
        double radius = 25.0D;
        int durationTicks = 160; // 8 seconds

        AABB scanArea = player.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, scanArea, e -> e != player && e.isAlive());

        if (!targets.isEmpty()) {
            for (LivingEntity target : targets) {
                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.GLOWING,
                        durationTicks,
                        0,
                        false,
                        false
                ));
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 0.8F, 1.2F);
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5F, 1.5F);
        }
    }

    public static void triggerAutoDischarge(ServerLevel level, Player player, ItemStack chestStack) {
        // Triggers a radial shockwave around the player
        VibraniumToolActions.spawnShockwave(level, player.position(), 6.0F, 1.5F, player);

        // Resets chestplate charge to zero
        chestStack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
    }

    //TOOLTIPS
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        Component useKey = Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage();
        // Common suit tooltip
        consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".black_panther_set_bonus")
                .withStyle(ChatFormatting.GRAY));

        // Specific tooltips per armor piece
        switch (this.armorType) {
            case HELMET -> {
                consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".black_panther_helmet_desc", useKey)
                        .withStyle(ChatFormatting.GRAY));
            }
            case CHESTPLATE -> {
                consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".black_panther_chestplate_desc")
                        .withStyle(ChatFormatting.GRAY));
            }
            case LEGGINGS -> {
                consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".black_panther_leggings_desc")
                        .withStyle(ChatFormatting.GRAY));
            }
            case BOOTS -> {

                consumer.accept(Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".black_panther_boots_desc")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }
}