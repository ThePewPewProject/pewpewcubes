package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.gamemode.GameMode;
import de.kleiner3.lasertag.lasertaggame.gamemode.GameModes;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IGameModeManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.GameModeState;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Implementation of the IGameModeManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class GameModeManager implements IGameModeManager {

    private final GameModeState gameModeState;

    private ISettingsManager settingsManager;

    private final MinecraftServer server;

    // Get path to lasertag game mode file
    private static final Path lasertagGameModeFilePath = LasertagMod.configFolderPath.resolve("lasertagGameMode.ltg");

    public GameModeManager(GameModeState gameModeState, MinecraftServer server) {

        this.gameModeState = gameModeState;
        this.server = server;

        if (Files.exists(lasertagGameModeFilePath)) {

            try {
                // Read the game mode file
                gameModeState.currentGameModeTranslatableName = Files.readString(lasertagGameModeFilePath);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of lasertag game mode file failed: " + e.getMessage());
            }
        } else {

            try {
                // Write to the file
                Files.writeString(lasertagGameModeFilePath, gameModeState.currentGameModeTranslatableName, StandardOpenOption.CREATE);
            } catch (IOException ex) {
                LasertagMod.LOGGER.warn("Creation of new lasertag game mode file in '" + lasertagGameModeFilePath + "' failed: " + ex.getMessage());
            }
        }
    }

    public void setSettingsManager(ISettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public void setGameMode(GameMode newGameMode) {
        gameModeState.currentGameModeTranslatableName = newGameMode.getTranslatableName();
        persist(newGameMode);
        sync(newGameMode);
        settingsManager.reset();
    }

    @Override
    public GameMode getGameMode() {
        return GameModes.GAME_MODES.get(gameModeState.currentGameModeTranslatableName);
    }

    private void persist(GameMode newGameMode) {

        try {
            Files.createDirectories(lasertagGameModeFilePath.getParent());
            Files.writeString(lasertagGameModeFilePath, newGameMode.getTranslatableName(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            LasertagMod.LOGGER.warn("Failed to persist lasertag game mode: " + ex.getMessage());
        }


    }

    private void sync(GameMode newGameMode) {

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(newGameMode.getTranslatableName());

        ServerEventSending.sendToEveryone(server, NetworkingConstants.GAME_MODE_SYNC, buf);
    }
}
