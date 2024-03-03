package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Callback for the client triggered player join team network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerPlayerJoinTeamCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var teamsManager = gameManager.getTeamsManager();
            var syncedState = gameManager.getSyncedState();
            var teamsConfigState = syncedState.getTeamsConfigState();

            // Read team id
            var teamId = buf.readInt();

            // Get the team
            var teamOptional = teamsConfigState.getTeamOfId(teamId);

            // Join team
            teamOptional.ifPresent(teamDto -> teamsManager.playerJoinTeam(player, teamDto));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerPlayerJoinTeamCallback", ex);
            throw ex;
        }
    }
}
