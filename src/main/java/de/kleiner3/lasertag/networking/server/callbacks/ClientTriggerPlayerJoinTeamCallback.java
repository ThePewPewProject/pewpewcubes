package de.kleiner3.lasertag.networking.server.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
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

            // Get the lasertag team manager
            var teamManager = LasertagGameManager.getInstance().getTeamManager();

            // Read team id
            var teamId = buf.readInt();

            // Get the team
            var teamOptional = teamManager.getTeamConfigManager().getTeamOfId(teamId);

            // Join team
            teamOptional.ifPresent(teamDto -> teamManager.playerJoinTeam(server.getOverworld(), teamDto, player));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerPlayerJoinTeamCallback", ex);
            throw ex;
        }
    }
}
