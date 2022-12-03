package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.client.hud.LasertagHudOverlay;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the progress network event
 * Used for the progress bar
 *
 * @author Étienne Muser
 */
public class ProgressCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        LasertagHudOverlay.renderData.progress = buf.readDouble();
    }
}
