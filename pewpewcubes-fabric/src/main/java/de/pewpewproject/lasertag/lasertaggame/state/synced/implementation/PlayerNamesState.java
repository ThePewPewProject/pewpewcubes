package de.pewpewproject.lasertag.lasertaggame.state.synced.implementation;

import de.pewpewproject.lasertag.lasertaggame.state.synced.IPlayerNamesState;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Implementation of IPlayerNamesState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class PlayerNamesState implements IPlayerNamesState {

    private final HashMap<UUID, String> playerUsernameCache = new HashMap<>();

    @Override
    public synchronized void savePlayerUsername(UUID playerUuid, String playerUsername) {
        playerUsernameCache.put(playerUuid, playerUsername);
    }

    @Override
    public synchronized String getPlayerUsername(UUID playerUuid) {
        return playerUsernameCache.get(playerUuid);
    }

    @Override
    public synchronized void forEachPlayer(Consumer<UUID> consumer) {

        for (var playerUuid : playerUsernameCache.keySet()) {

            consumer.accept(playerUuid);
        }
    }
}
