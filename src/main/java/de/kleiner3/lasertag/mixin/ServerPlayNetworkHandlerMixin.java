package de.kleiner3.lasertag.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into the ServerPlayerNetworkHandler.class to disable the "Player moved too quickly" warning while
 * loading a new map
 *
 * @author Étienne Muser
 */
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Inject(method = "onPlayerMove(Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket;)V", at = @At("HEAD"), cancellable = true)
    private void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {

        // Get the game managers
        var gameManager = ((ServerPlayNetworkHandler)(Object)this).server.getOverworld().getServerLasertagManager();
        var arenaManager = gameManager.getArenaManager();

        if (arenaManager.isLoading()) {
            ci.cancel();
        }
    }
}
