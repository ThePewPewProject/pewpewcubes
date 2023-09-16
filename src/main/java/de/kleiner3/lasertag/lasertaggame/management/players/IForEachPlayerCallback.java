package de.kleiner3.lasertag.lasertaggame.management.players;

import java.util.UUID;

/**
 * Functional interface for the callback of LasertagPlayerNameManager.forEachPlayer()
 *
 * @author Étienne Muser
 */
public interface IForEachPlayerCallback {
    void execute(UUID playerUuid);
}
