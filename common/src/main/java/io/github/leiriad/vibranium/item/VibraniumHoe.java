package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import io.github.leiriad.vibranium.utils.VibraniumToolMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class VibraniumHoe extends HoeItem {

    private static final ThreadLocal<Boolean> IS_HARVESTING_AREA = ThreadLocal.withInitial(() -> false);
    private static final float COST_PER_EXTRA_TILL = 0.5F;
    private static final float COST_PER_HARVEST_PULSE = 5.0F;

    public VibraniumHoe(Properties properties) {
        super(VibraniumToolMaterial.VIBRANIUM, -3.0F, 0.0F, properties);
    }

    public static Item.Properties getProperties(Item.Properties settings) {
        Item.Properties props = settings.enchantable(22);

        props.attributes(
                ItemAttributeModifiers.builder()
                        .add(
                                Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, -3.0F, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                Attributes.ATTACK_SPEED,
                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, 0.0F, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .build()
        );

        return props;
    }

    /**
     * Accumulates kinetic charge on block break.
     * Triggers Sonic Harvest Pulse when breaking fully grown crops.
     */
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && miner instanceof Player player && !IS_HARVESTING_AREA.get()) {

            // Build kinetic charge on primary block broken
            float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            float updatedCharge = Math.min(100.0F, currentCharge + 10.0F);
            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), updatedCharge);

            // Sonic Harvest effect when breaking a mature crop
            if (state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(state)) {
                if (updatedCharge >= COST_PER_HARVEST_PULSE) {
                    IS_HARVESTING_AREA.set(true);
                    try {
                        triggerSonicHarvestPulse(level, pos, player, stack);
                    } finally {
                        IS_HARVESTING_AREA.set(false);
                    }
                }
            }
        }

        return super.mineBlock(stack, level, state, pos, miner);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();

        // Shift + Right Click on block -> Toggle Kinetic Burst (3x3 mode)
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                toggleKineticBurstMode(level, player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        // Standard Right Click on block -> Resonant Tilling (1x1 or 3x3 depending on mode)
        if (context.getClickedFace() != Direction.DOWN) {
            BlockState targetState = level.getBlockState(clickedPos);

            if (TILLABLES.containsKey(targetState.getBlock()) && level.getBlockState(clickedPos.above()).isAir()) {
                if (!level.isClientSide()) {
                    boolean burstActive = stack.getOrDefault(VibraniumDataComponents.RESONANCE_MODE.get(), false);
                    EquipmentSlot slot = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

                    if (burstActive) {
                        burstTill(level, clickedPos, player, stack, slot);
                    } else {
                        // Standard vanilla tilling behavior
                        var pair = TILLABLES.get(targetState.getBlock());
                        if (pair != null && pair.getFirst().test(context)) {
                            pair.getSecond().accept(context);
                            level.playSound(player, clickedPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                            stack.hurtAndBreak(1, player, slot);
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Shift + Right Click in air -> Toggle Kinetic Burst (3x3 mode)
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                toggleKineticBurstMode(level, player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    /**
     * Toggles the 3x3 Kinetic Burst mode manually.
     */
    private void toggleKineticBurstMode(Level level, Player player, ItemStack stack) {
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        boolean currentMode = stack.getOrDefault(VibraniumDataComponents.RESONANCE_MODE.get(), false);
        boolean newState = !currentMode;

        if (newState) {
            if (currentCharge < COST_PER_EXTRA_TILL) {
                return;
            }

            stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 1.5F);
            player.displayClientMessage(
                    Component.translatable("tooltip.vibranium.tool.mode.active").withStyle(ChatFormatting.LIGHT_PURPLE), true);
        } else {
            stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.8F, 1.2F);
            player.displayClientMessage(
                    Component.translatable("tooltip.vibranium.tool.mode.inactive").withStyle(ChatFormatting.GRAY), true);
        }
    }

    /**
     * Tills a 3x3 area using vanilla tilling logic.
     */
    private void burstTill(Level level, BlockPos center, Player player, ItemStack stack, EquipmentSlot slot) {
        int radius = 1;
        boolean tilledAny = false;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

                if (x != 0 || z != 0) {
                    if (currentCharge < COST_PER_EXTRA_TILL) {
                        continue;
                    }
                }

                BlockPos targetPos = center.offset(x, 0, z);
                BlockState state = level.getBlockState(targetPos);

                // Check if block can be tilled
                if (TILLABLES.containsKey(state.getBlock()) && level.getBlockState(targetPos.above()).isAir()) {
                    var pair = TILLABLES.get(state.getBlock());

                    if (pair != null) {
                        // In 1.21+, the tilled block state is obtained via changeIntoState
                        BlockState tilledState = net.minecraft.world.level.block.Blocks.FARMLAND.defaultBlockState();
                        level.setBlock(targetPos, tilledState, Block.UPDATE_ALL);
                        tilledAny = true;

                        if (x != 0 || z != 0) {
                            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - COST_PER_EXTRA_TILL);
                        }
                    }
                }
            }
        }

        if (tilledAny) {
            level.playSound(player, center, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            stack.hurtAndBreak(1, player, slot);
        }
    }

    /**
     * Emits a sonic shockwave harvesting nearby mature crops without breaking seeds or young plants.
     */
    private void triggerSonicHarvestPulse(Level level, BlockPos center, Player player, ItemStack stack) {
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

        stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - COST_PER_HARVEST_PULSE);

        if (level instanceof ServerLevel serverLevel) {
            VibraniumToolActions.spawnShockwave(serverLevel, center.getBottomCenter(), 4.0F, 0.2F, player);
        }

        int radius = 4;
        BlockPos.betweenClosedStream(center.offset(-radius, -1, -radius), center.offset(radius, 1, radius))
                .forEach(targetPos -> {
                    BlockState targetState = level.getBlockState(targetPos);
                    if (targetState.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(targetState)) {
                        // Harvest block safely while leaving seed intact
                        level.destroyBlock(targetPos, true, player);
                        level.setBlock(targetPos, cropBlock.getStateForAge(0), Block.UPDATE_ALL);
                    }
                });

        level.playSound(null, center, SoundEvents.BRUSH_SAND_COMPLETED, SoundSource.PLAYERS, 0.8F, 1.6F);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    /* --- Kinetic Charge Bar --- */

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

        consumer.accept(
                Component.translatable("tooltip.vibranium.weapons.charge", (int) charge)
                        .withStyle((charge > 0) ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY)
        );

        consumer.accept(
                Component.translatable(statusKey)
                        .withStyle(statusColor, ChatFormatting.BOLD)
        );

        consumer.accept(
                Component.translatable("tooltip.vibranium.tool.active.toggle")
                        .withStyle(ChatFormatting.GRAY)
        );

        consumer.accept(
                Component.translatable("tooltip.vibranium.hoe.active.resonant_till")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
        );

        consumer.accept(
                Component.translatable("tooltip.vibranium.hoe.active.sonic_harvest")
                        .withStyle(ChatFormatting.GOLD)
        );

        consumer.accept(
                Component.translatable("tooltip.vibranium.hoe.passive.sculk_suppression")
                        .withStyle(ChatFormatting.DARK_AQUA)
        );

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}