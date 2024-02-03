package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.client.screen.LasertagTeamSelectorScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the team config reloaded network event
 *
 * @author Étienne Muser
 */
public class TeamConfigReloadedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();
            var teamConfigState = gameManager.getSyncedState().getTeamsConfigState();

            var jsonString = buf.readString();

            // Deserialize and set team config
            teamConfigState.setTeamConfig(jsonString);

            if (client.currentScreen instanceof LasertagTeamSelectorScreen lasertagTeamSelectorScreen) {
                lasertagTeamSelectorScreen.resetList();
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in TeamConfigReloadedCallback", ex);
            throw ex;
        }
    }
}
