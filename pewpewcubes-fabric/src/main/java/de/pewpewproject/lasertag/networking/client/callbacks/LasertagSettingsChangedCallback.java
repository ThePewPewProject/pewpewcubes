package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.client.screen.LasertagGameManagerSettingsScreen;
import de.pewpewproject.lasertag.lasertaggame.gamemode.GameModes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the lasertag settings changed network event
 *
 * @author Étienne Muser
 */
public class LasertagSettingsChangedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManger = client.world.getClientLasertagManager();
            var settingsManager = gameManger.getSettingsManager();
            var gameModeManager = gameManger.getGameModeManager();

            var gameModeName = buf.readString();
            var newSettingsJson = buf.readString();

            // If the game mode is desynced (should normally never happen)
            if (!gameModeManager.getGameMode().getTranslatableName().equals(gameModeName)) {

                // Set the game mode
                gameModeManager.setGameMode(GameModes.GAME_MODES.get(gameModeName));
            }

            settingsManager.set(newSettingsJson);

            if (client.currentScreen instanceof LasertagGameManagerSettingsScreen lasertagGameManagerSettingsScreen) {
                lasertagGameManagerSettingsScreen.resetList();
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in LasertagSettingsChangedCallback", ex);
            throw ex;
        }
    }
}
