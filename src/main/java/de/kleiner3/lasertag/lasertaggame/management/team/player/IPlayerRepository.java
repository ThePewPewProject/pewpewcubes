package de.kleiner3.lasertag.lasertaggame.management.team.player;

import java.util.UUID;

/**
 * Interface for consistent username access even if the player is not on the server anymore
 *
 * @author Étienne Muser
 */
public interface IPlayerRepository {
    default String getConsistentPlayerUsername(UUID uuid) {
        return null;
    }
}
