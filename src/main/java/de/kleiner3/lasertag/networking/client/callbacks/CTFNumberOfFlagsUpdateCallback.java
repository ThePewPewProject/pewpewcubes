package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
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
            var gameManager = LasertagGameManager.getInstance();
            var flagManager = gameManager.getFlagManager();
            var teamManger = gameManager.getTeamManager().getTeamConfigManager();

            // Get the team
            var teamOptional = teamManger.getTeamOfId(buf.readInt());

            // Get the new number of flags
            var newNumberOfFlags = buf.readLong();

            // Update flag count
            teamOptional.ifPresent(team -> flagManager.updateTeamFlagCount(team, newNumberOfFlags));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in CTFNumberOfFlagsUpdateCallback", ex);
            throw ex;
        }
    }
}
