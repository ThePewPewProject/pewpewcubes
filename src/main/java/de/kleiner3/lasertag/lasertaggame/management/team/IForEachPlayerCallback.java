package de.kleiner3.lasertag.lasertaggame.management.team;

import java.util.UUID;

/**
 * Functional interface for the callback of LasertagTeamManager.forEachPlayer()
 *
 * @author Étienne Muser
 */
public interface IForEachPlayerCallback {

    void execute(TeamDto team, UUID playerUuid);
}
