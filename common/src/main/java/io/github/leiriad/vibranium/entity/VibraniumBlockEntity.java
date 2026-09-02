package io.github.leiriad.vibranium.entity;

import io.github.leiriad.vibranium.block.VibraniumBlock;
import io.github.leiriad.vibranium.init.VibraniumEntities;
import io.github.leiriad.vibranium.utils.VibraniumToolActions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VibraniumBlockEntity extends BlockEntity {

    private int charge = 0;
    private static final int MAX_CHARGE = 10;

    public VibraniumBlockEntity(BlockPos pos, BlockState state) {
        super(VibraniumEntities.VIBRANIUM_BLOCK_ENTITY.get(), pos, state);
    }

    public void addCharge(int amount) {
        this.charge = Math.min(MAX_CHARGE, this.charge + amount);
        if (this.level != null) {
            this.level.setBlock(this.worldPosition, this.getBlockState().setValue(VibraniumBlock.CHARGE, this.charge), 3);
            this.setChanged();
        }
    }

    public int getCharge() {
        return this.charge;
    }

    public void releaseKineticEnergy(ServerLevel serverLevel, Player triggerer) {
        if (this.charge <= 0) return;

        // Radius scales with stored charge (e.g., 3.0 to 7.0 blocks)
        float radius = 2.0F + (this.charge * 0.65F);
        float force = 0.5F + (this.charge * 0.12F);

        Vec3 center = Vec3.atCenterOf(this.worldPosition);

        //Call shockwave
        spawnBlockShockwave(serverLevel, center, radius, force, triggerer);
        

        // Reset charge state
        this.charge = 0;
        serverLevel.setBlock(this.worldPosition, this.getBlockState().setValue(VibraniumBlock.CHARGE, 0), 3);
        this.setChanged();
    }

    private void spawnBlockShockwave(ServerLevel level, Vec3 center, float radius, float force, Player attacker) {
        // Visual effects
        int points = 48;
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;

            level.sendParticles(
                    ParticleTypes.EXPLOSION,
                    center.x + dx, center.y + 0.5, center.z + dz,
                    1, dx * 0.15, 0.05, dz * 0.15, 0.1
            );
            // Add some sonic particles for flavor
            level.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    center.x + (dx * 0.5), center.y + 0.5, center.z + (dz * 0.5),
                    1, 0, 0, 0, 0
            );
        }

        // Play Warden sonic boom
        level.playSound(null, center.x, center.y, center.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 2.0F, 1.0F);


        // Damage & Knockback entities
        double diameter = radius * 2.0;
        AABB area = AABB.ofSize(center, diameter, diameter, diameter);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != attacker && e.isAlive());

        for (LivingEntity target : targets) {
            Vec3 direction = target.position().subtract(center);
            double distance = direction.length();

            if (distance <= radius && distance > 0.001) {
                Vec3 knockback = direction.normalize().scale((1.0 - (distance / radius)) * force);
                target.setDeltaMovement(target.getDeltaMovement().add(knockback.x, 0.35, knockback.z));
                target.hurtMarked = true;
            }
        }

        // Breaking & harvesting blocks
        BlockPos centerPos = BlockPos.containing(center);
        int blockRadius = Math.round(radius);

        for (BlockPos pos : BlockPos.betweenClosed(
                centerPos.offset(-blockRadius, -1, -blockRadius),
                centerPos.offset(blockRadius, 2, blockRadius))) {

            // Check if within spherical radius
            if (pos.distSqr(centerPos) <= radius * radius) {
                BlockState state = level.getBlockState(pos);

                // Ignore air, bedrock and unbreakable blocks
                if (!state.isAir() && state.getDestroySpeed(level, pos) >= 0) {

                    // Special check for crops: only harvest if fully grown
                    if (state.is(BlockTags.CROPS)) {
                        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                            level.destroyBlock(pos, true, attacker); // Breaks and drops items
                        }
                    }
                    // For all other blocks (Stone, Dirt, Wood, Ores, etc.)
                    // Exclude bedrock/indestructibles via blast resistance check
                    else if (state.getBlock().getExplosionResistance() < 1200.0F) {
                        // destroyBlock with 'true' guarantees all drops are looted
                        level.destroyBlock(pos, true, attacker);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("Charge", this.charge);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.charge = valueInput.getInt("Charge").orElse(0);
    }

    // --- NETWORK SYNCHRONIZATION ---

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
}