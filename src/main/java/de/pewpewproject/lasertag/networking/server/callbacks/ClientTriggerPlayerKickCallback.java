package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Callback for the client trigger kick player network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerPlayerKickCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity ignoredSender, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var teamsManager = gameManager.getTeamsManager();

            var playerUuid = buf.readUuid();

            teamsManager.playerLeaveHisTeam(playerUuid);

            // Try to get the player from the player manager
            var player = server.getPlayerManager().getPlayer(playerUuid);

            if (player != null) {
                player.getInventory().clear();
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerPlayerKickCallback", ex);
            throw ex;
        }
    }
}
