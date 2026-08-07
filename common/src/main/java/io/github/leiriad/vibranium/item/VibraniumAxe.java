package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import io.github.leiriad.vibranium.utils.VibraniumToolMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class VibraniumAxe extends AxeItem {

    private static final ThreadLocal<Boolean> IS_MINING_AREA = ThreadLocal.withInitial(() -> false);
    private static final float COST_PER_EXTRA_BLOCK = 2.0F;
    private static final float CLEAVE_CHARGE_COST = 25.0F;

    public VibraniumAxe(Properties properties) {
        super(VibraniumToolMaterial.VIBRANIUM, 6.0F, -3.1F, properties);
    }

    public static Item.Properties getProperties(Item.Properties settings) {
        Item.Properties props = settings.enchantable(22);

        props.attributes(
                ItemAttributeModifiers.builder()
                        .add(
                                Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 8.0F, AttributeModifier.Operation.ADD_VALUE), // Direct heavy damage
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                Attributes.ATTACK_SPEED,
                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.1F, AttributeModifier.Operation.ADD_VALUE), // Heavy attack speed
                                EquipmentSlotGroup.MAINHAND
                        )
                        .build()
        );

        return props;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && miner instanceof Player player && !IS_MINING_AREA.get()) {

            boolean isBurstActive = stack.getOrDefault(VibraniumDataComponents.RESONANCE_MODE.get(), false);

            if (isBurstActive) {
                // RESONANCE MODE: Consume charge
                if (state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.LEAVES)) {
                    float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
                    if (currentCharge < COST_PER_EXTRA_BLOCK) {
                        stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), false);
                    } else {
                        IS_MINING_AREA.set(true);
                        try {
                            burstMine(level, pos, player, stack);
                        } finally {
                            IS_MINING_AREA.set(false);
                        }
                    }
                }
            } else {
                // NORMAL MODE: Gain charge when mining valid blocks
                float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

                if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
                    // Full charge gain for wood logs
                    stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), Math.min(100.0F, currentCharge + 5.0F));
                } else if (state.is(BlockTags.LEAVES)) {
                    // Reduced charge gain for leaves
                    stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), Math.min(100.0F, currentCharge + 1.0F));
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, miner);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.level() instanceof ServerLevel level) {
            float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), Math.min(100.0F, currentCharge + 8.0F));

            if (target.isBlocking() && currentCharge >= 15.0F) {
                stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - 15.0F);
                if (target instanceof Player targetPlayer) {
                    var blockingItem = targetPlayer.getItemBlockingWith();
                    if (!blockingItem.isEmpty()) {
                        var blocksAttacks = blockingItem.get(net.minecraft.core.component.DataComponents.BLOCKS_ATTACKS);
                        if (blocksAttacks != null) {
                            blocksAttacks.disable(level, targetPlayer, 5.0F, blockingItem);
                        }
                    }
                }
                level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SHIELD_BREAK.value(), SoundSource.PLAYERS, 1.0F, 0.8F);
            }
        }
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide() && player.level() instanceof ServerLevel serverLevel) {
            if (tryTriggerDirectionalCleave(serverLevel, player, stack)) {
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, interactionTarget, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();

        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                toggleKineticBurstMode(level, player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            if (tryTriggerDirectionalCleave(serverLevel, player, stack)) {
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                toggleKineticBurstMode(level, player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            if (tryTriggerDirectionalCleave(serverLevel, player, stack)) {
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(level, player, hand);
    }

    /**
     * Triggers a linear seismic wave in front of the player.
     */
    private boolean tryTriggerDirectionalCleave(ServerLevel level, Player player, ItemStack stack) {
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

        if (charge >= CLEAVE_CHARGE_COST) {
            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), charge - CLEAVE_CHARGE_COST);

            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.6F);

            Vec3 look = player.getLookAngle().normalize();
            Vec3 start = player.position().add(0, 0.5, 0);

            for (int i = 1; i <= 6; i++) {
                Vec3 point = start.add(look.scale(i));
                BlockPos targetPos = BlockPos.containing(point);

                VibraniumToolActions.spawnShockwave(level, point, 1.5F, 0.2F, player);

                AABB box = new AABB(targetPos).inflate(1.0);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player).forEach(entity -> {
                    entity.hurt(level.damageSources().playerAttack(player), 7.0F);
                    entity.setDeltaMovement(look.x * 1.0, 0.45, look.z * 1.0);
                    entity.hurtMarked = true;
                });
            }
            return true;
        }
        return false;
    }

    private void burstMine(Level level, BlockPos center, Player player, ItemStack stack) {
        EquipmentSlot slot = EquipmentSlot.MAINHAND;

        // Trigger a visual and sound shockwave centered on the broken block
        if (level instanceof ServerLevel serverLevel) {
            VibraniumToolActions.spawnShockwave(serverLevel, center.getBottomCenter(), 1.5F, 0.2F, player);
        }

        // Iterate through a full 3x3x3 cube around the targeted block
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    // Ignore the central block already broken by mineBlock
                    if (x == 0 && y == 0 && z == 0) continue;

                    BlockPos targetPos = center.offset(x, y, z);
                    breakBlockWithCost(level, targetPos, player, stack, slot);
                }
            }
        }
    }

    private void breakBlockWithCost(Level level, BlockPos pos, Player player, ItemStack stack, EquipmentSlot slot) {
        BlockState state = level.getBlockState(pos);
        // Skip air and non-matching blocks immediately without consuming charge or stopping the loop
        if (state.isAir() || (!state.is(BlockTags.MINEABLE_WITH_AXE) && !state.is(BlockTags.LEAVES))) {
            return;
        }
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        // Stop breaking extra blocks if charge is depleted
        if (currentCharge < COST_PER_EXTRA_BLOCK) {
            return;
        }
        // Break valid blocks (wood or leaves) and deduct kinetic charge
        stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - COST_PER_EXTRA_BLOCK);
        level.destroyBlock(pos, true, player);
        stack.hurtAndBreak(1, player, slot);
    }

    private void toggleKineticBurstMode(Level level, Player player, ItemStack stack) {
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        boolean currentMode = stack.getOrDefault(VibraniumDataComponents.RESONANCE_MODE.get(), false);
        boolean newState = !currentMode;

        if (newState) {
            if (currentCharge < COST_PER_EXTRA_BLOCK) return;
            stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 1.5F);
            player.displayClientMessage(Component.translatable("tooltip.vibranium.tool.mode.active").withStyle(ChatFormatting.LIGHT_PURPLE), true);
        } else {
            stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.8F, 1.2F);
            player.displayClientMessage(Component.translatable("tooltip.vibranium.tool.mode.inactive").withStyle(ChatFormatting.GRAY), true);
        }
    }
    private void disablePlayerShield(ServerLevel level, Player targetPlayer) {
        ItemStack blockingItem = targetPlayer.getItemBlockingWith();
        if (!blockingItem.isEmpty()) {
            BlocksAttacks blocksAttacks = blockingItem.get(DataComponents.BLOCKS_ATTACKS);
            if (blocksAttacks != null) {
                // Applies a cooldown/disable time to the item used for parrying
                float cooldownSeconds = targetPlayer.getSecondsToDisableBlocking();
                float duration = cooldownSeconds > 0.0F ? cooldownSeconds : 5.0F; // 5 seconds
                blocksAttacks.disable(level, targetPlayer, duration, blockingItem);
            }
        }
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
        return 0x9933FF;
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
        boolean burstActive = stack.getOrDefault(VibraniumDataComponents.RESONANCE_MODE.get(), false);

        ChatFormatting statusColor = burstActive ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.DARK_GRAY;
        String statusKey = burstActive ? "tooltip.vibranium.tool.mode.active" : "tooltip.vibranium.tool.mode.inactive";

        consumer.accept(Component.translatable("tooltip.vibranium.weapons.charge", (int) charge).withStyle((charge > 0) ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY));
        consumer.accept(Component.translatable(statusKey).withStyle(statusColor, ChatFormatting.BOLD));
        consumer.accept(Component.translatable("tooltip.vibranium.tool.active.toggle").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.translatable("tooltip.vibranium.axe.active.cleave").withStyle(ChatFormatting.LIGHT_PURPLE));
        consumer.accept(Component.translatable("tooltip.vibranium.axe.passive.shield_break").withStyle(ChatFormatting.DARK_GREEN));

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}