package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the player joined network event
 *
 * @author Étienne Muser
 */
public class PlayerJoinedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();
            var playerNameState = gameManager.getSyncedState().getPlayerNamesState();

            var playerUuid = buf.readUuid();
            var playerName = buf.readString();

            playerNameState.savePlayerUsername(playerUuid, playerName);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in PlayerJoinedCallback", ex);
            throw ex;
        }
    }
}
