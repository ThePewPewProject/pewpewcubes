package de.pewpewproject.lasertag.lasertaggame.state.management.server.implementation;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.IStartGamePermissionManager;
import de.pewpewproject.lasertag.lasertaggame.state.server.implementation.StartGamePermissionState;
import net.minecraft.entity.player.PlayerEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Implementation of IStartGamePermissionManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class StartGamePermissionManager implements IStartGamePermissionManager {

    private final StartGamePermissionState state;

    private static final Path lasertagStartGamePermittedPlayersFilePath = LasertagMod.configFolderPath.resolve("startGamePermittedPlayers.json");

    public StartGamePermissionManager() {
        StartGamePermissionState tmpState = null;

        if (Files.exists(lasertagStartGamePermittedPlayersFilePath)) {

            try {
                var fileContents = Files.readString(lasertagStartGamePermittedPlayersFilePath);

                tmpState = StartGamePermissionState.fromJson(fileContents);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of start game permitted players file failed: " + e.getMessage());
                tmpState = new StartGamePermissionState();
            }
        } else {
            tmpState = new StartGamePermissionState();
        }

        state = tmpState;
    }

    @Override
    public boolean isStartGamePermitted(PlayerEntity player) {
        return state.contains(player.getUuid());
    }

    @Override
    public void setStartGamePermitted(PlayerEntity player) {
        state.add(player.getUuid());
        persist();
    }

    @Override
    public void setNotStartGamePermitted(PlayerEntity player) {
        state.remove(player.getUuid());
        persist();
    }

    private void persist() {
        try {
            Files.createDirectories(lasertagStartGamePermittedPlayersFilePath.getParent());
            Files.writeString(lasertagStartGamePermittedPlayersFilePath, state.toJson(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            LasertagMod.LOGGER.error("Could not persist start game permitted players file: " + ex.getMessage());
        }
    }
}
