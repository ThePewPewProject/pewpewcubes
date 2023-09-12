package de.kleiner3.lasertag.lasertaggame.management.players;

import de.kleiner3.lasertag.lasertaggame.management.IManager;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class to manage the lasertag players. This is necessary because players could disconnect
 * but still should be referencable.
 *
 * @author Étienne Muser
 */
public class LasertagPlayerNameManager implements IManager {

    private final HashMap<UUID, String> playerUsernameCache;

    public LasertagPlayerNameManager() {
        playerUsernameCache = new HashMap<>();
    }

    public void putPlayer(UUID playerUuid, String playerUsername) {
        playerUsernameCache.put(playerUuid, playerUsername);
    }

    public String getPlayerUsername(UUID uuid) {
        return playerUsernameCache.get(uuid);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    /**
     * Executes the given callback for every player currently registered.
     *
     * @param callback
     */
    public void forEachPlayer(IForEachPlayerCallback callback) {

        for (var playerUuid : playerUsernameCache.keySet()) {
            callback.execute(playerUuid);
        }
    }
}
