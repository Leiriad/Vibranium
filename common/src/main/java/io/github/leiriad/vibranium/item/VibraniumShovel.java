package io.github.leiriad.vibranium.item;

import io.github.leiriad.vibranium.VibraniumMod;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class VibraniumShovel extends ShovelItem {

    // ThreadLocal guard to prevent recursive execution loops when mining a 3x3 area
    private static final ThreadLocal<Boolean> IS_MINING_AREA = ThreadLocal.withInitial(() -> false);
    private static final float COST_PER_EXTRA_BLOCK = 2.0F;
    private static final float COST_PER_PATH_BLOCK = 0.5F; // Low charge cost for flattening paths

    public VibraniumShovel(Properties properties) {
        super(VibraniumToolMaterial.VIBRANIUM, 4.5F, -3.0F, properties);
    }

    public static Item.Properties getProperties(Item.Properties settings) {
        Item.Properties props = settings.enchantable(22);

        props.attributes(
                ItemAttributeModifiers.builder()
                        .add(
                                Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 4.5F, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                Attributes.ATTACK_SPEED,
                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.0F, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .build()
        );

        return props;
    }

    /**
     * Accumulates kinetic charge on block break and triggers 3x3 Kinetic Burst if active.
     * Passive effect: Automatically collapses falling blocks (Sand, Gravel) above the mined block.
     */
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && miner instanceof Player player && !IS_MINING_AREA.get()) {

            // Build kinetic charge on primary block broken
            float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            float updatedCharge = Math.min(100.0F, currentCharge + 5.0F);
            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), updatedCharge);

            boolean isBurstActive = stack.getOrDefault(VibraniumDataComponents.RESONANCE_MODE.get(), false);

            if (isBurstActive) {
                if (updatedCharge < COST_PER_EXTRA_BLOCK) {
                    stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), false);
                } else if (isSoftEarth(state)) {
                    IS_MINING_AREA.set(true);
                    try {
                        burstMineSurface(level, pos, player, stack);
                    } finally {
                        IS_MINING_AREA.set(false);
                    }
                }
            }

            // Passive feature: Gravity neutralizer for falling block columns
            if (state.getBlock() instanceof FallingBlock) {
                BlockPos abovePos = pos.above();
                BlockState aboveState = level.getBlockState(abovePos);

                while (aboveState.getBlock() instanceof FallingBlock) {
                    level.destroyBlock(abovePos, true, player);
                    abovePos = abovePos.above();
                    aboveState = level.getBlockState(abovePos);
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

        // Standard Right Click on block -> Dirt path creation (1x1 or 3x3 depending on mode)
        if (context.getClickedFace() != Direction.DOWN) {
            BlockState targetState = level.getBlockState(clickedPos);
            BlockState pathState = FLATTENABLES.get(targetState.getBlock());

            if (pathState != null && level.getBlockState(clickedPos.above()).isAir()) {
                if (!level.isClientSide()) {
                    boolean burstActive = stack.getOrDefault(VibraniumDataComponents.RESONANCE_MODE.get(), false);
                    EquipmentSlot slot = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

                    if (burstActive) {
                        create3x3PathArea(level, clickedPos, player, stack, slot);
                    } else {
                        // Vanilla behaviour (1x1 path)
                        level.setBlock(clickedPos, pathState, Block.UPDATE_ALL);
                        level.playSound(player, clickedPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                        stack.hurtAndBreak(1, player, slot);
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

        // Maintain blocking stance on normal right click in air
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    /**
     * Toggles the 3x3 Kinetic Burst mode manually.
     * Refuses activation if current kinetic charge is too low.
     */
    private void toggleKineticBurstMode(Level level, Player player, ItemStack stack) {
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        boolean currentMode = stack.getOrDefault(VibraniumDataComponents.RESONANCE_MODE.get(), false);
        boolean newState = !currentMode;

        if (newState) {
            // Refuse activation if charge is insufficient
            if (currentCharge < COST_PER_EXTRA_BLOCK) {
                return;
            }

            stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 1.5F);
            player.displayClientMessage(
                    Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.active").withStyle(ChatFormatting.LIGHT_PURPLE), true);
        } else {
            stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.8F, 1.2F);
            player.displayClientMessage(
                    Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.inactive").withStyle(ChatFormatting.GRAY), true);
        }
    }


    /**
     * Mines horizontal/planar surface areas based on player gaze angle.
     * Prevents unintended destruction of floors and lower wall blocks.
     */
    private void burstMineSurface(Level level, BlockPos center, Player player, ItemStack stack) {
        Direction direction = player.getDirection();
        Direction.Axis axis = direction.getAxis();
        EquipmentSlot slot = EquipmentSlot.MAINHAND;

        if (level instanceof ServerLevel serverLevel) {
            VibraniumToolActions.spawnShockwave(serverLevel, center.getBottomCenter(), 2.5F, 0.3F, player);
        }

        if (axis == Direction.Axis.Y) {
            // Player looking UP/DOWN: clear 3x3 horizontal plane
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;
                    if (!breakBlockWithChargeCost(level, center.offset(x, 0, z), player, stack, slot)) {
                        stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), false);
                        return;
                    }
                }
            }
        } else {
            // Player looking CARDINAL (N/S/E/W): clear horizontal surface line (depth x width)
            Direction right = direction.getClockWise();

            for (int depth = -1; depth <= 2; depth++) {
                for (int width = -1; width <= 1; width++) {
                    if (depth == 0 && width == 0) continue;
                    BlockPos targetPos = center.relative(direction, depth).relative(right, width);
                    if (!breakBlockWithChargeCost(level, targetPos, player, stack, slot)) {
                        stack.set(VibraniumDataComponents.RESONANCE_MODE.get(), false);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Breaks a target block within the 3x3 area if kinetic charge is sufficient.
     * Returns false when charge is depleted.
     */
    private boolean breakBlockWithChargeCost(Level level, BlockPos pos, Player player, ItemStack stack, EquipmentSlot slot) {
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

        if (currentCharge < COST_PER_EXTRA_BLOCK) {
            return false;
        }

        BlockState targetState = level.getBlockState(pos);
        if (isSoftEarth(targetState)) {
            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - COST_PER_EXTRA_BLOCK);
            level.destroyBlock(pos, true, player);
            stack.hurtAndBreak(1, player, slot);
        }

        return true;
    }

    /**
     * Creates a 3x3 dirt path area consuming low kinetic charge (0.5F per extra path block).
     */
    private void create3x3PathArea(Level level, BlockPos center, Player player, ItemStack stack, EquipmentSlot slot) {
        int radius = 1;
        boolean createdAny = false;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

                // Allow central block for free, require cost for surrounding path blocks
                if (x != 0 || z != 0) {
                    if (currentCharge < COST_PER_PATH_BLOCK) {
                        continue;
                    }
                }

                BlockPos targetPos = center.offset(x, 0, z);
                BlockState state = level.getBlockState(targetPos);
                BlockState pathState = FLATTENABLES.get(state.getBlock());

                if (pathState != null && level.getBlockState(targetPos.above()).isAir()) {
                    level.setBlock(targetPos, pathState, Block.UPDATE_ALL);
                    createdAny = true;

                    if (x != 0 || z != 0) {
                        stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - COST_PER_PATH_BLOCK);
                    }
                }
            }
        }

        if (createdAny) {
            level.playSound(player, center, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            stack.hurtAndBreak(1, player, slot);
        }
    }

    private boolean isSoftEarth(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(Blocks.SNOW)
                || state.is(Blocks.SNOW_BLOCK);
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
        return 0x9933FF; // Purple charge bar
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
        String statusKey = burstActive ? "tooltip." + VibraniumMod.MOD_ID + ".tool.mode.active" : "tooltip." + VibraniumMod.MOD_ID + ".tool.mode.inactive";

        consumer.accept(
                Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".weapons.charge", (int) charge)
                        .withStyle((charge > 0) ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY)
        );
        consumer.accept(
                Component.translatable(statusKey)
                        .withStyle(statusColor, ChatFormatting.BOLD)
        );
        consumer.accept(
                Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".tool.active.toggle")
                        .withStyle(ChatFormatting.GRAY)
        );
        consumer.accept(
                Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".shovel.passive.gravity")
                        .withStyle(ChatFormatting.GRAY)
        );

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}