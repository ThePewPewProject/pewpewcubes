package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.client.screen.LasertagGameManagerTeamsScreen;
import de.pewpewproject.lasertag.client.screen.LasertagTeamSelectorScreen;
import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the team update network event
 *
 * @author Étienne Muser
 */
public class TeamUpdateCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();
            var teamsManager = gameManager.getTeamsManager();
            var syncedState = gameManager.getSyncedState();
            var teamsConfigState = syncedState.getTeamsConfigState();

            var playerUuid = buf.readUuid();


            var newValueString = buf.readString();
            TeamDto newValue = null;
            if (!newValueString.equals("null")) {

                newValue = teamsConfigState.getTeamOfId(Integer.parseInt(newValueString)).orElseThrow();
            }

            if (newValue == null) {
                teamsManager.removePlayerFromTeam(playerUuid);
            } else {
                teamsManager.updateTeamOfPlayer(playerUuid, newValue);
            }

            if (client.currentScreen instanceof LasertagGameManagerTeamsScreen lasertagGameManagerTeamsScreen) {
                lasertagGameManagerTeamsScreen.resetList();
            }

            if (client.currentScreen instanceof LasertagTeamSelectorScreen lasertagTeamSelectorScreen) {
                lasertagTeamSelectorScreen.resetList();
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in TeamUpdateCallback", ex);
            throw ex;
        }
    }
}
