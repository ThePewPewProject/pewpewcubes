package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.client.LasertagHudOverlay;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the LivintEntityRenderer class
 *
 * @author Étienne Muser
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    /**
     * Inject into the hasLabel method to disable displaying name tags during a lasertag game
     * @param cir The CallbackInfoReturnable
     */
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void onHasLabel(CallbackInfoReturnable<Boolean> cir) {
        if (!LasertagHudOverlay.shouldRenderNameTags) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
