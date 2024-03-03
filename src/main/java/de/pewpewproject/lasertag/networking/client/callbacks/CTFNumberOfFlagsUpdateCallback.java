package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the capture the flag number of flags update network event
 *
 * @author Étienne Muser
 */
public class CTFNumberOfFlagsUpdateCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the managers
            var gameManager = client.world.getClientLasertagManager();
            var captureTheFlagManager = gameManager.getCaptureTheFlagManager();
            var syncedState = gameManager.getSyncedState();
            var teamsManger = syncedState.getTeamsConfigState();

            // Get the team
            var teamOptional = teamsManger.getTeamOfId(buf.readInt());

            // Get the new number of flags
            var newNumberOfFlags = buf.readLong();

            // Update flag count
            teamOptional.ifPresent(team -> captureTheFlagManager.updateTeamFlagCount(team, newNumberOfFlags));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in CTFNumberOfFlagsUpdateCallback", ex);
            throw ex;
        }
    }
}
