package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the player deactivation status changed network event
 *
 * @author Étienne Muser
 */
public class PlayerDeactivatedStatusChangedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();
            var activationManager = gameManager.getActivationManager();

            // Read from buffer
            var uuid = buf.readUuid();
            var deactivated = buf.readBoolean();

            // Set deactivated status
            activationManager.setDeactivated(uuid, deactivated);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in PlayerDeactivatedStatusChangedCallback", ex);
            throw ex;
        }
    }
}
