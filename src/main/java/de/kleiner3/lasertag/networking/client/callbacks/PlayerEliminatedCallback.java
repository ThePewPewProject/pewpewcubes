package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the player eliminated network event
 *
 * @author Étienne Muser
 */
public class PlayerEliminatedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var clientManager = client.world.getClientLasertagManager();
            var eliminationManager = clientManager.getEliminationManager();

            // Get the data from the buffer
            var eliminatedPlayerUuid = buf.readUuid();
            var shooterUuid = buf.readNullable(PacketByteBuf::readUuid);
            var newEliminationCount = buf.readLong();
            var playerSurviveTime = buf.readLong();

            // Set the data on the elimination manager
            eliminationManager.setPlayerEliminated(eliminatedPlayerUuid);
            eliminationManager.setEliminationCount(shooterUuid, newEliminationCount);
            eliminationManager.setPlayerSurviveTime(eliminatedPlayerUuid, playerSurviveTime);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in PlayerEliminatedCallback", ex);
            throw ex;
        }
    }
}
