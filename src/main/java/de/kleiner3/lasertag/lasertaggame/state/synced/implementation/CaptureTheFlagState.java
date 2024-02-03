package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.synced.ICaptureTheFlagState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ICaptureTheFlagState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class CaptureTheFlagState implements ICaptureTheFlagState {

    /**
     * Maps every team's id to the number of flags they have left
     */
    private final HashMap<Integer, Long> teamFlagMap = new HashMap<>();

    /**
     * Maps every team's id to the time in seconds they survived
     */
    private final HashMap<Integer, Long> teamSurviveTimeMap = new HashMap<>();

    /**
     * Maps every player to the number of flags he has captured. Only necessary on the server. Does not get
     * synced to the clients.
     */
    private final HashMap<UUID, Long> playerFlagCapturedMap = new HashMap<>();

    /**
     * Maps every player to the team id of the team he is currently holding the flag of. If a player is currently
     * not holding a flag, he has no entry in this map.
     */
    private final HashMap<UUID, Integer> playerHoldingFlagMap = new HashMap<>();

    @Override
    public synchronized void reset() {
        this.teamFlagMap.clear();
        this.teamSurviveTimeMap.clear();
        this.playerFlagCapturedMap.clear();
        this.playerHoldingFlagMap.clear();
    }

    @Override
    public synchronized void playerDropFlag(UUID playerUuid) {

        // If player is not holding a flag
        if (!playerHoldingFlagMap.containsKey(playerUuid)) {

            // Do nothing
            return;
        }

        // Remove the flag the player is holding
        playerHoldingFlagMap.remove(playerUuid);
    }

    @Override
    public synchronized void playerPickupFlag(UUID playerUuid, int team) {

        // Set the player holding the flag
        playerHoldingFlagMap.put(playerUuid, team);
    }

    @Override
    public synchronized Optional<Integer> getPlayerHoldingFlagTeam(UUID playerUuid) {
        return Optional.ofNullable(playerHoldingFlagMap.get(playerUuid));
    }

    @Override
    public synchronized long getNumberOfCapturedFlags(UUID playerUuid) {
        return Optional.ofNullable(playerFlagCapturedMap.get(playerUuid)).orElse(0L);
    }

    @Override
    public synchronized void setNumberOfCapturedFlags(UUID playerUuid, long newFlagCapturedCount) {
        playerFlagCapturedMap.put(playerUuid, newFlagCapturedCount);
    }

    @Override
    public synchronized Optional<Long> getSurviveTime(TeamDto team) {
        return Optional.ofNullable(teamSurviveTimeMap.get(team.id()));
    }

    @Override
    public synchronized void setSurviveTime(TeamDto team, long surviveTime) {
        teamSurviveTimeMap.put(team.id(), surviveTime);
    }

    @Override
    public synchronized void updateTeamFlagCount(TeamDto team, long newFlagCount) {
        teamFlagMap.put(team.id(), newFlagCount);
    }

    @Override
    public synchronized long getNumberOfFlags(TeamDto team) {
        return Optional.ofNullable(teamFlagMap.get(team.id())).orElse(0L);
    }
}
