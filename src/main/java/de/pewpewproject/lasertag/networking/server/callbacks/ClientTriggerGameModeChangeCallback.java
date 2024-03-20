package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.lasertaggame.gamemode.GameModes;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Callback for the client trigger game mode change network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerGameModeChangeCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var gameModeManager = gameManager.getGameModeManager();

            // If a game is running
            if (gameManager.isGameRunning()) {
                // Cannot change game mode in-game
                return;
            }

            // Get the game mode translatable name
            var newGameModeTranslatableName = buf.readString();

            // Get the new game mode
            var newGameMode = GameModes.GAME_MODES.get(newGameModeTranslatableName);

            // Set the new game mode
            gameModeManager.setGameMode(newGameMode);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerGameModeChangeCallback", ex);
            throw ex;
        }
    }
}
