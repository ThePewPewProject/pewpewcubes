package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.client.screen.LasertagGameManagerScreen;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.GameModes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the game mode sync network event
 *
 * @author Étienne Muser
 */
public class GameModeSyncCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        // Get the game mode translatable name
        var gameModeTranslatableName = buf.readString();

        // Get the new game mode
        var newGameMode = GameModes.GAME_MODES.get(gameModeTranslatableName);

        // Set the new game mode
        LasertagGameManager.getInstance().getGameModeManager().setGameMode(null, newGameMode);

        // If client has game manager screen open
        if (client.currentScreen instanceof LasertagGameManagerScreen lasertagGameManagerScreen) {
            lasertagGameManagerScreen.reloadGameMode();
        }
    }
}
