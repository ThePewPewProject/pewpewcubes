package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.client.screen.LasertagGameManagerTeamsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into the ClientPlayNetworkHandlerMixin.class to refresh the lasertag game manger gui if player joins / leaves
 *
 * @author Étienne Muser
 */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onPlayerList(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;)V", at = @At("RETURN"))
    private void onPlayerListUpdate(PlayerListS2CPacket packet, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof LasertagGameManagerTeamsScreen lasertagGameManagerTeamsScreen) {
            lasertagGameManagerTeamsScreen.resetList();
        }
    }
}
