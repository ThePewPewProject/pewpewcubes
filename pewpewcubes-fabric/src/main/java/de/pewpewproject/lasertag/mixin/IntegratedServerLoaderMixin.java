package de.pewpewproject.lasertag.mixin;

import net.minecraft.server.integrated.IntegratedServerLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Removes the experimental features warning when re-entering a world
 *
 * Modified version of <a href="https://github.com/rdvdev2/DisableCustomWorldsAdvice">DisableCustomWorldsAdvice</a>.
 */
@Mixin(IntegratedServerLoader.class)
public class IntegratedServerLoaderMixin {

    // Set canShowBackupPrompt = false
    @ModifyVariable(
            method = "start(Lnet/minecraft/client/gui/screen/Screen;Ljava/lang/String;ZZ)V",
            at = @At("HEAD"),
            argsOnly = true,
            index = 4
    )
    private boolean removeAdviceOnLoad(boolean original) {
        return false;
    }
}
