package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the score update network event
 *
 * @author Étienne Muser
 */
public class ScoreUpdateCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        // Get parameters
        var playerUuid = buf.readUuid();
        var newValue = buf.readLong();

        LasertagGameManager.getInstance().getScoreManager().updateScore(playerUuid, newValue);
    }
}
