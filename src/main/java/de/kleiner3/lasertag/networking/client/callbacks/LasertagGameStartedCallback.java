package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the lasertag game started network event
 *
 * @author Étienne Muser
 */
public class LasertagGameStartedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the gameManagers
            var gameManager = client.world.getClientLasertagManager();
            var uiManager = gameManager.getUIStateManager();
            var settingsManager = gameManager.getSettingsManager();
            var uiState = gameManager.getSyncedState().getUIState();

            uiState.isGameRunning = true;

            // Start pregame count down timer
            uiManager.startPreGameCountdownTimer(settingsManager.<Long>get(SettingDescription.PREGAME_DURATION));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in LasertagGameStartedCallback", ex);
            throw ex;
        }
    }
}
