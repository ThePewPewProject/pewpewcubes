package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.SyncedState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the game manager sync network event
 *
 * @author Étienne Muser
 */
public class LasertagStateSyncCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Wait for the world to load
            while(client.world == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();

            // Get json string
            var jsonString = buf.readString();

            // Deserialize
            var state = SyncedState.fromJson(jsonString);

            // Set
            gameManager.setSyncedState(state);

            // Get the render config
            var uiState = gameManager.getSyncedState().getUIState();
            var uiManager = gameManager.getUIStateManager();

            // If game is running
            if (uiState.isGameRunning) {
                // If pre game countdown is already over
                if (uiState.startingIn <= -1) {
                    // Start game timer
                    uiManager.startGameTimer(uiState.gameTime);
                } else {
                    // Start pre game timer
                    uiManager.startPreGameCountdownTimer(uiState.startingIn);
                }
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in LasertagGameManagerSyncCallback", ex);
            throw ex;
        }
    }
}
