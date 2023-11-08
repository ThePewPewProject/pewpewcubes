package de.kleiner3.lasertag.lasertaggame.management.gamemode;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.FileIO;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.LasertagSettingsMap;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

/**
 * Manages the lasertag game mode.
 *
 * @author Étienne Muser
 */
public class LasertagGameModeManager implements IManager {

    // Get path to lasertag game mode file
    private static final String lasertagGameModeFilePath = LasertagMod.configFolderPath + "\\lasertagGameMode.ltg";

    // Create file object
    private static final File lasertagGameModeFile = new File(lasertagGameModeFilePath);

    // Store the current game mode as a string to work around some wierd GSON stuff
    private String currentGameModeTranslatableName = GameModes.POINT_HUNTER_GAME_MODE.getTranslatableName();

    public LasertagGameModeManager() {

        if (lasertagGameModeFile.exists()) {

            try {

                // Read the game mode file
                this.currentGameModeTranslatableName = FileIO.readAllFile(lasertagGameModeFile);
            } catch (IOException e) {

                LasertagMod.LOGGER.warn("Reading of lasertag game mode file failed: " + e.getMessage());
            }
        } else {

            try {

                // Create the game mode file
                var newFile = FileIO.createNewFile(lasertagGameModeFilePath);

                // Write to the file
                FileIO.writeAllFile(newFile, currentGameModeTranslatableName);
            } catch (IOException ex) {

                LasertagMod.LOGGER.warn("Creation of new lasertag game mode file in '" + lasertagGameModeFilePath + "' failed: " + ex.getMessage());
            }
        }
    }

    public void setGameMode(MinecraftServer s, GameMode newGameMode) {
        this.currentGameModeTranslatableName = newGameMode.getTranslatableName();
        persist(s, newGameMode);
        LasertagGameManager.getInstance().getSettingsManager().reset(s);
    }

    public GameMode getGameMode() {
        return GameModes.GAME_MODES.get(this.currentGameModeTranslatableName);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    private static void persist(MinecraftServer server, GameMode newGameMode) {

        // Check if this is executed on client
        if (server == null) {
            // Do not sync on client
            return;
        }

        try {
            FileIO.writeAllFile(lasertagGameModeFile, newGameMode.getTranslatableName());
        } catch (IOException ex) {
            LasertagMod.LOGGER.warn("Failed to persist lasertag game mode: " + ex.getMessage());
        }

        sync(server, newGameMode);
    }

    private static void sync(MinecraftServer server, GameMode newGameMode) {

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(newGameMode.getTranslatableName());

        ServerEventSending.sendToEveryone(server.getOverworld(), NetworkingConstants.GAME_MODE_SYNC, buf);
    }
}
