package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the lasertag game over network event
 *
 * @author Étienne Muser
 */
public class LasertagGameOverCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();
            var uiManager = gameManager.getUIStateManager();
            var uiState = gameManager.getSyncedState().getUIState();

            uiManager.stopGameTimer();
            uiState.isGameRunning = false;
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in LasertagGameOverCallback", ex);
            throw ex;
        }
    }
}
