package de.pewpewproject.lasertag.mixin;

import net.minecraft.client.sound.MusicTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the MusicTracer class to disable the default minecraft music
 *
 * @author Étienne Muser
 */
@Mixin(MusicTracker.class)
public abstract class MusicTrackerMixin {
    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    private void atTick(CallbackInfo ci) {
        // Return method immediately
        ci.cancel();
    }

    @Inject(method = "play(Lnet/minecraft/sound/MusicSound;)V", at = @At("HEAD"), cancellable = true)
    private void atPlay(CallbackInfo ci) {
        // Return method immediately
        ci.cancel();
    }

    @Inject(method = "stop()V", at = @At("HEAD"), cancellable = true)
    private void atStop(CallbackInfo ci) {
        // Return method immediately
        ci.cancel();
    }

    @Inject(method = "isPlayingType(Lnet/minecraft/sound/MusicSound;)Z", at = @At("HEAD"), cancellable = true)
    private void isPlayingType(CallbackInfoReturnable<Boolean> cir) {
        // MusicTracker cant play music anymore - immediately return false
        cir.setReturnValue(false);
    }
}
