package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.client.screen.LasertagLoadingScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the map loading network event
 *
 * @author Étienne Muser
 */
public class MapLoadingEventCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();
            var uiState = gameManager.getSyncedState().getUIState();

            var stepString = buf.readString();
            var newProgress = buf.readDouble();

            var oldProgress = uiState.mapLoadingProgress;
            uiState.mapLoadingStepString = stepString;
            uiState.mapLoadingProgress = newProgress;

            if (oldProgress == -1 && newProgress >= 0) {
                client.execute(() -> client.setScreen(new LasertagLoadingScreen()));
                return;
            }

            if (oldProgress >= 0 && newProgress == -1) {

                if (client.currentScreen instanceof LasertagLoadingScreen loadingScreen) {
                    loadingScreen.stopSpinner();
                }

                client.execute(() -> client.setScreen(null));
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in MapLoadingEventCallback", ex);
            throw ex;
        }
    }
}
