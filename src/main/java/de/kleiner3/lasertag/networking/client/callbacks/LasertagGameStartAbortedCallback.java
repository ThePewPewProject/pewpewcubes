package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the game start aborted event
 *
 * @author Étienne Muser
 */
public class LasertagGameStartAbortedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // Get the hud manager
        var renderData = LasertagGameManager.getInstance().getHudRenderManager();

        // Reset progress back to 0
        renderData.progress = 0.0;
    }
}
