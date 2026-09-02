package io.github.leiriad.vibranium.mixin.client;

import io.github.leiriad.vibranium.client.soundmanagement.VibraniumSoundFilter;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Shadow @Final private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow abstract float calculateVolume(SoundInstance soundInstance);

    /**
     * Called on every client tick to continuously update volumes for all playing sounds.
     */
    @Inject(method = "tickInGameSound", at = @At("TAIL"))
    private void vibranium$updateRealtimeVolumes(CallbackInfo ci) {
        if (this.instanceToChannel.isEmpty()) {
            return;
        }

        this.instanceToChannel.forEach((soundInstance, handle) -> {
            if (soundInstance != null && !handle.isStopped()) {
                handle.execute(channel -> {
                    // Recalculate original volume and multiply by attenuation factor
                    float baseVolume = this.calculateVolume(soundInstance);
                    float multiplier = VibraniumSoundFilter.getVolumeMultiplierForInstance(soundInstance);

                    channel.setVolume(baseVolume * multiplier);
                });
            }
        });
    }
}
