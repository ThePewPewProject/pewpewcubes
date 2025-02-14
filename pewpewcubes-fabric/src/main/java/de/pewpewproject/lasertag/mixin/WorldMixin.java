package de.pewpewproject.lasertag.mixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Étienne Muser
 */
@Mixin(World.class)
public abstract class WorldMixin {

    @Inject(method = "tickBlockEntities()V", at = @At("HEAD"))
    private void onTickBlockEntities(CallbackInfo ci) {

        // Do not tick on clients
        if (((World)(Object)this).isClient) {
            return;
        }

        ((World)(Object)this).getServer().getOverworld().getServerLasertagManager().getBlockTickManager().tick();
    }
}
