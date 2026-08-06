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
     * Passive — Kinetic Charge Accumulation & Gravity Neutralisation:
     * Gains kinetic charge when mining blocks. If mining a falling block (Sand, Gravel),
     * automatically collapses the entire column above safely.
     */
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && miner instanceof Player player) {
            // Accumulate kinetic charge on block break
            float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            if (currentCharge < 100.0F) {
                stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), Math.min(100.0F, currentCharge + 5.0F));
            }

            // Gravity neutralisation logic
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

    /**
     * Handles Right-Click actions:
     * - Shift + Right-Click: Energy Dispersal (Requires min. charge & triggers shockwave)
     * - Right-Click in air: Blocking stance (Absorbs kinetic energy)
     * - Right-Click on block: 3x3 Dirt Path creation
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        EquipmentSlot slot = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        ItemStack stack = context.getItemInHand();

        // Active — Energy Dispersal (Shift + Right Click)
        if (player.isSecondaryUseActive()) {
            float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            if (charge >= 10.0F) {
                if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                    performEnergyDispersal(serverLevel, clickedPos, player, stack, slot, charge);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }

        // Passive Bonus — 3x3 Dirt Path Creation (Standard Right Click on valid block)
        if (context.getClickedFace() != Direction.DOWN) {
            BlockState targetState = level.getBlockState(clickedPos);
            BlockState pathState = FLATTENABLES.get(targetState.getBlock());

            if (pathState != null && level.getBlockState(clickedPos.above()).isAir()) {
                if (!level.isClientSide()) {
                    createPathArea(level, clickedPos, player, stack, slot);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    /**
     * Clears a 3x3 plane aligned with player's look vector and triggers the kinetic shockwave.
     */
    private void performEnergyDispersal(ServerLevel level, BlockPos center, Player player, ItemStack stack, EquipmentSlot slot, float charge) {
        Direction direction = player.getDirection();
        Direction.Axis axis = direction.getAxis();
        boolean damaged = false;

        if (axis == Direction.Axis.Y) {
            // Player is looking Up or Down: Clear a 3x3 horizontal surface (Ground/Ceiling)
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos targetPos = center.offset(x, 0, z);
                    if (isSoftEarth(level.getBlockState(targetPos))) {
                        level.destroyBlock(targetPos, true, player);
                        damaged = true;
                    }
                }
            }
        } else {
            // Player is looking straight ahead (North/South/East/West):
            // Only clear a 3-wide horizontal line at target height (prevents unintended wall/floor destruction)
            Direction right = direction.getClockWise();

            for (int depth = -1; depth <= 2; depth++) {       // Forward / Backward along look direction
                for (int width = -1; width <= 1; width++) {   // Left / Right perpendicular to look direction
                    BlockPos targetPos = center.relative(direction, depth).relative(right, width);
                    if (isSoftEarth(level.getBlockState(targetPos))) {
                        level.destroyBlock(targetPos, true, player);
                        damaged = true;
                    }
                }
            }
        }

        // Trigger shockwave scaling with charge energy
        float radius = 2.0F + (charge / 30.0F);
        float force = 0.8F + (charge / 50.0F);
        VibraniumToolActions.spawnShockwave(level, center.getBottomCenter(), radius, force, player);

        // Reset charge and apply durability damage
        stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        if (damaged) {
            stack.hurtAndBreak(1, player, slot);
        }
    }

    /**
     * Converts a 3x3 area of dirt/grass into dirt paths.
     */
    private void createPathArea(Level level, BlockPos center, Player player, ItemStack stack, EquipmentSlot slot) {
        int radius = 1;
        boolean createdAny = false;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos targetPos = center.offset(x, 0, z);
                BlockState state = level.getBlockState(targetPos);
                BlockState pathState = FLATTENABLES.get(state.getBlock());

                if (pathState != null && level.getBlockState(targetPos.above()).isAir()) {
                    level.setBlock(targetPos, pathState, Block.UPDATE_ALL);
                    createdAny = true;
                }
            }
        }

        if (createdAny) {
            level.playSound(
                    player,
                    center,
                    SoundEvents.SHOVEL_FLATTEN,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
            stack.hurtAndBreak(1, player, slot);
        }
    }

    private boolean isSoftEarth(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(Blocks.SNOW)
                || state.is(Blocks.SNOW_BLOCK);
    }

    /* --- Energy Charge Bar Custom Rendering --- */

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
        consumer.accept(
                Component.translatable("tooltip.vibranium.shovel.passive.gravity")
                        .withStyle(ChatFormatting.GRAY)
        );
        consumer.accept(
                Component.translatable("tooltip.vibranium.shovel.passive.path")
                        .withStyle(ChatFormatting.GRAY)
        );
        consumer.accept(
                Component.translatable("tooltip.vibranium.shovel.active.dispersal")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
        );

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}