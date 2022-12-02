package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.client.LasertagHudOverlay;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the lasertag game over network event
 *
 * @author Étienne Muser
 */
public class LasertagGameOverCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        synchronized (LasertagHudOverlay.gameTimerLock) {
            if (LasertagHudOverlay.gameTimer != null) {
                LasertagHudOverlay.gameTimer.shutdown();
                LasertagHudOverlay.gameTimer = null;
                LasertagHudOverlay.gameTime = 0;
            }
        }

        LasertagHudOverlay.shouldRenderNameTags = true;
    }
}
