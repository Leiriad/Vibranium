package io.github.leiriad.vibranium.item;

import dev.architectury.networking.NetworkManager;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.client.event.KeyInputHandler;
import io.github.leiriad.vibranium.config.VibraniumConfigManager;
import io.github.leiriad.vibranium.network.OreHighlightPayload;
import io.github.leiriad.vibranium.utils.VibraniumDataComponents;
import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import io.github.leiriad.vibranium.utils.VibraniumToolMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VibraniumPickaxe extends Item implements VibraniumAbilityItem{

    //ThreadLocal guard to prevent recursive execution loops when mining a 3x3 area
    private static final ThreadLocal<Boolean> IS_MINING_AREA = ThreadLocal.withInitial(() -> false);
    private static final float COST_PER_EXTRA_BLOCK = VibraniumConfigManager.INSTANCE.tools.pickaxeBurstCostPerBlock;
    private static final float SONAR_COST = VibraniumConfigManager.INSTANCE.tools.pickaxeSonarCost;
    private static final int SONAR_RADIUS = VibraniumConfigManager.INSTANCE.tools.pickaxeSonarRadius;
    private static final int SONAR_DURATION = VibraniumConfigManager.INSTANCE.tools.pickaxeSonarDurationTicks;

    public VibraniumPickaxe(Properties properties) {
        super(properties);
    }

    public static Item.Properties getProperties(Item.Properties settings) {
        return settings
                .pickaxe(VibraniumToolMaterial.VIBRANIUM, 1.0F, -2.8F)
                .enchantable(22);
    }
    /**
     * Handles key press packet from server (NetworkManager receiver).
     * Toggles 3x3 Kinetic Burst mode.
     */
    @Override
    public void onAbilityKeyPressed(Player player, ItemStack stack) {
        if (player.level() instanceof ServerLevel serverLevel) {
            toggleKineticBurstMode(serverLevel, player, stack);
        }
    }
    /**
     * Accumulates kinetic charge on block break and triggers 3x3 Kinetic Burst if active.
     */
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && miner instanceof Player player && !IS_MINING_AREA.get()) {

            //Build kinetic charge on primary block broken
            float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
            float updatedCharge = Math.min(100.0F, currentCharge + 5.0F);
            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), updatedCharge);

            boolean isBurstActive = stack.getOrDefault(VibraniumDataComponents.BURST_MODE.get(), false);

            if (isBurstActive) {
                if (updatedCharge < COST_PER_EXTRA_BLOCK) {
                    stack.set(VibraniumDataComponents.BURST_MODE.get(), false);
                } else if (isSafeMiningTarget(state)) {
                    IS_MINING_AREA.set(true);
                    try {
                        burstMine(level, pos, player, stack);
                    } finally {
                        IS_MINING_AREA.set(false);
                    }
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, miner);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();

        //Standard useKey on Block -> Trigger Sonar Pulse (Echolocation)
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            if (tryTriggerSonarPulse(serverLevel, context.getClickedPos(), player, stack)) {
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            //Standard useKey in air -> Trigger Sonar Pulse (Echolocation)
            if (tryTriggerSonarPulse(serverLevel, player.blockPosition(), player, stack)) {
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(level, player, hand);
    }

    /**
     * Toggles the 3x3 Kinetic Burst mining mode.
     */
    private void toggleKineticBurstMode(Level level, Player player, ItemStack stack) {
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        boolean currentMode = stack.getOrDefault(VibraniumDataComponents.BURST_MODE.get(), false);
        boolean newState = !currentMode;

        if (newState) {
            //Refuse activation if charge is insufficient
            if (currentCharge < COST_PER_EXTRA_BLOCK) {
                return;
            }

            stack.set(VibraniumDataComponents.BURST_MODE.get(), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 1.5F);
            player.displayClientMessage(
                    Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.active").withStyle(ChatFormatting.LIGHT_PURPLE), true);
        } else {
            stack.set(VibraniumDataComponents.BURST_MODE.get(), false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.8F, 1.2F);
            player.displayClientMessage(
                    Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.inactive").withStyle(ChatFormatting.GRAY), true);
        }
    }

    /**
     * Mines blocks in a 3x3 area around the broken block and triggers a kinetic shockwave effect.
     */
    private void burstMine(Level level, BlockPos center, Player player, ItemStack stack) {
        Direction direction = player.getDirection();
        Direction.Axis axis = direction.getAxis();
        EquipmentSlot slot = EquipmentSlot.MAINHAND;

        if (level instanceof ServerLevel serverLevel) {
            VibraniumToolActions.spawnShockwave(serverLevel, center.getBottomCenter(), 3.0F, 0.3F, player);
        }

        if (axis == Direction.Axis.Y) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;
                    if (!breakBlockWithChargeCost(level, center.offset(x, 0, z), player, stack, slot)) {
                        stack.set(VibraniumDataComponents.BURST_MODE.get(), false); // Coupe le mode
                        return;
                    }
                }
            }
        } else {
            Direction right = direction.getClockWise();
            for (int y = -1; y <= 1; y++) {
                for (int width = -1; width <= 1; width++) {
                    if (y == 0 && width == 0) continue;
                    BlockPos targetPos = center.above(y).relative(right, width);
                    if (!breakBlockWithChargeCost(level, targetPos, player, stack, slot)) {
                        stack.set(VibraniumDataComponents.BURST_MODE.get(), false); // Coupe le mode
                        return;
                    }
                }
            }
        }
    }

    /**
     * Breaks a target block within the 3x3 area if sufficient kinetic charge is available
     * and the block is a safe target.
     */
    private boolean breakBlockWithChargeCost(Level level, BlockPos pos, Player player, ItemStack stack, EquipmentSlot slot) {
        float currentCharge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

        if (currentCharge < COST_PER_EXTRA_BLOCK) {
            return false; //Charge insuffisante
        }

        BlockState targetState = level.getBlockState(pos);

        if (isSafeMiningTarget(targetState)) {
            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), currentCharge - COST_PER_EXTRA_BLOCK);
            level.destroyBlock(pos, true, player);
            stack.hurtAndBreak(1, player, slot);
        }

        return true;
    }

    private boolean isSafeMiningTarget(BlockState state) {
        if (isOre(state)) {
            return false;
        }
        return state.is(BlockTags.STONE_ORE_REPLACEABLES)
                || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.BASE_STONE_NETHER);
    }

    /**
     * Triggers Sonar Pulse (Echolocation) to scan nearby ores and highlight entities. Costs 20 Kinetic Charge.
     */
    private boolean tryTriggerSonarPulse(ServerLevel level, BlockPos center, Player player, ItemStack stack) {
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);

        if (charge >= SONAR_COST) {
            stack.set(VibraniumDataComponents.KINETIC_CHARGE.get(), charge - SONAR_COST);

            //Sonar sound effect
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 0.8F, 1.2F);

            //Scan ores within radius
            int radius = SONAR_RADIUS;
            int durationTicks = SONAR_DURATION;
            List<BlockPos> foundOres = new ArrayList<>();

            for (BlockPos targetPos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
                BlockState state = level.getBlockState(targetPos);
                if (isOre(state)) {
                    foundOres.add(targetPos.immutable());
                }
            }

            //Send packet to client for rendering highlights
            if (player instanceof ServerPlayer serverPlayer && !foundOres.isEmpty()) {
                NetworkManager.sendToPlayer(serverPlayer, new OreHighlightPayload(foundOres, durationTicks));
            }

            //Highlight nearby entities with Glowing effect
            AABB area = new AABB(center).inflate(radius);
            level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player).forEach(entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, durationTicks, 0, false, false));
            });

            return true;
        }

        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        Component abilityKey = KeyInputHandler.itemAbilityKey.getTranslatedKeyMessage();
        Component useKey = Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage();
        float charge = stack.getOrDefault(VibraniumDataComponents.KINETIC_CHARGE.get(), 0.0F);
        boolean burstActive = stack.getOrDefault(VibraniumDataComponents.BURST_MODE.get(), false);

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
                Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".tool.active.toggle", abilityKey, useKey)
                        .withStyle(ChatFormatting.GRAY)
        );
        consumer.accept(
                Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".pickaxe.active.echolocation", useKey)
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
        );
        consumer.accept(
                Component.translatable("tooltip." + VibraniumMod.MOD_ID + ".pickaxe.passive.filter")
                        .withStyle(ChatFormatting.DARK_GREEN)
        );

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }


    private boolean isOre(BlockState state) {
        return state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.DIAMOND_ORES)
                || state.is(BlockTags.REDSTONE_ORES)
                || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.EMERALD_ORES)
                || state.is(BlockTags.COPPER_ORES);
    }
}