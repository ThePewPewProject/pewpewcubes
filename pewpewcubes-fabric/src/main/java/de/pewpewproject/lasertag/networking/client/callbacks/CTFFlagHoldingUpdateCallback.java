package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the capture the flag flag holding update network event
 *
 * @author Étienne Muser
 */
public class CTFFlagHoldingUpdateCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();
            var captureTheFlagManager = gameManager.getCaptureTheFlagManager();

            // Get the player uuid
            var playerUuid = buf.readUuid();

            // Get the team id
            Integer teamId = null;
            var teamIdString = buf.readString();
            if (!teamIdString.equals("null")) {
                teamId = Integer.parseInt(teamIdString);
            }

            // Set the player holding flag
            if (teamId == null) {
                captureTheFlagManager.removeFlagHolding(playerUuid);
            } else {
                captureTheFlagManager.updateFlagHolding(playerUuid, teamId);
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in CTFFlagHoldingUpdateCallback", ex);
            throw ex;
        }
    }
}
