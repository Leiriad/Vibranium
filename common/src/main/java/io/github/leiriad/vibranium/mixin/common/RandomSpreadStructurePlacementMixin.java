package io.github.leiriad.vibranium.mixin.common;

import io.github.leiriad.vibranium.config.VibraniumConfigManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomSpreadStructurePlacement.class)
public abstract class RandomSpreadStructurePlacementMixin {

    @Inject(method = "getPotentialStructureChunk", at = @At("HEAD"), cancellable = true)
    private void overrideStructureSpread(long seed, int x, int z, CallbackInfoReturnable<ChunkPos> cir) {
        // Use the Accessor interface to safely bypass the protected access barrier
        StructurePlacementAccessor accessor = (StructurePlacementAccessor) this;
        RandomSpreadStructurePlacement instance = (RandomSpreadStructurePlacement) (Object) this;

        int currentSalt = accessor.getSalt();

        // Check if it matches your permanent configuration IDs
        if (currentSalt == 12345678) {
            int customSpacing = VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.spacing;
            int customSeparation = VibraniumConfigManager.INSTANCE.meteoritesOverworld.placement.separation;

            cir.setReturnValue(calculateChunkPos(instance, seed, x, z, customSpacing, customSeparation, currentSalt));
        } else if (currentSalt == 87654321) {
            int customSpacing = VibraniumConfigManager.INSTANCE.meteoritesEnd.placement.spacing;
            int customSeparation = VibraniumConfigManager.INSTANCE.meteoritesEnd.placement.separation;

            cir.setReturnValue(calculateChunkPos(instance, seed, x, z, customSpacing, customSeparation, currentSalt));
        }
    }

    @Unique
    private ChunkPos calculateChunkPos(RandomSpreadStructurePlacement instance, long seed, int x, int z, int spacing, int separation, int salt) {
        int k = Math.floorDiv(x, spacing);
        int m = Math.floorDiv(z, spacing);
        net.minecraft.world.level.levelgen.WorldgenRandom worldgenRandom = new net.minecraft.world.level.levelgen.WorldgenRandom(new net.minecraft.world.level.levelgen.LegacyRandomSource(0L));
        worldgenRandom.setLargeFeatureWithSalt(seed, k, m, salt);
        int n = spacing - separation;
        int o = instance.spreadType().evaluate(worldgenRandom, n);
        int p = instance.spreadType().evaluate(worldgenRandom, n);
        return new ChunkPos(k * spacing + o, m * spacing + p);
    }
}
