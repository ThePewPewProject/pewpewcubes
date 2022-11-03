package de.kleiner3.lasertag.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Inject into the PlayerManager class of minecraft to get the event that a player left / joined the game
 *
 * @author Étienne Muser
 */
@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    /**
     * Inject into method remove() at the end of the method
     *
     * @param player
     * @param ci
     */
    @Inject(method = "remove", at = @At("TAIL"))
    private void onPlayerRemoved(ServerPlayerEntity player, CallbackInfo ci) {
        ((PlayerManager) (Object) this).getServer().playerLeaveHisTeam(player);
    }
}
