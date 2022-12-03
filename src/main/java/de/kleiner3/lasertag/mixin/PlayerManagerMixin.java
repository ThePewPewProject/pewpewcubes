package de.kleiner3.lasertag.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
     * @param player The player who is removed
     * @param ci The CallbackInfo
     */
    @Inject(method = "remove", at = @At("TAIL"))
    private void onPlayerRemoved(ServerPlayerEntity player, CallbackInfo ci) {
        ((PlayerManager) (Object) this).getServer().playerLeaveHisTeam(player);
    }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerJoined(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        player.requestTeleport(0.5, 1, 0.5);
    }

    @Inject(method = "respawnPlayer", at = @At("HEAD"))
    private void onRespawn(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        player.setSpawnPoint(((PlayerManager)(Object)this).getServer().getOverworld().getRegistryKey(), BlockPos.ORIGIN, 0.0F, true, false);
    }
}
