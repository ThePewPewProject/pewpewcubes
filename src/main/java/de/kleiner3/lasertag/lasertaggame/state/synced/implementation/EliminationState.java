package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.synced.IEliminationState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of IEliminationState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class EliminationState implements IEliminationState {

    /**
     * Map every player to the number of player he has eliminated
     *     key: The players uuid
     *     value: The number of players he eliminated
     */
    private final Map<UUID, Long> playerEliminationCountMap = new ConcurrentHashMap<>();

    /**
     * Set of all players that have been eliminated
     */
    private final Set<UUID> eliminatedPlayersSet = new HashSet<>();

    /**
     * Set of all teams that have been eliminated. Teams are saved by their ids.
     */
    private final Set<Integer> eliminatedTeamsSet = new HashSet<>();

    /**
     * Map mapping every eliminated team to their survive time
     *     key: The teams id
     *     value: The time in seconds the team got eliminated
     */
    private final Map<Integer, Long> teamSurviveTimeMap = new ConcurrentHashMap<>();

    /**
     * Map mapping every players uuid to their survive time in seconds
     *     key: The players uuid
     *     value: The players survive time in seconds
     */
    private final Map<UUID, Long> playerSurviveTimeMap = new ConcurrentHashMap<>();

    @Override
    public void setEliminationCount(UUID playerUuid, long newCount) {
        playerEliminationCountMap.put(playerUuid, newCount);
    }

    @Override
    public long getEliminationCount(UUID playerUuid) {
        return Optional.ofNullable(playerEliminationCountMap.get(playerUuid)).orElse(0L);
    }

    @Override
    public void eliminatePlayer(UUID playerUuid) {
        eliminatedPlayersSet.add(playerUuid);
    }

    @Override
    public boolean isPlayerEliminated(UUID playerUuid) {
        return eliminatedPlayersSet.contains(playerUuid);
    }

    @Override
    public void eliminateTeam(int teamId) {
        eliminatedTeamsSet.add(teamId);
    }

    @Override
    public boolean isTeamEliminated(int teamId) {
        return eliminatedTeamsSet.contains(teamId);
    }

    @Override
    public void setTeamSurviveTime(int teamId, long surviveTime) {
        teamSurviveTimeMap.put(teamId, surviveTime);
    }

    @Override
    public Long getTeamSurviveTime(int teamId) {
        return teamSurviveTimeMap.get(teamId);
    }

    @Override
    public void setPlayerSurviveTime(UUID playerUuid, long surviveTime) {
        playerSurviveTimeMap.put(playerUuid, surviveTime);
    }

    @Override
    public Long getPlayerSuriviveTime(UUID playerUuid) {
        return playerSurviveTimeMap.get(playerUuid);
    }

    @Override
    public synchronized void reset() {
        playerEliminationCountMap.clear();
        eliminatedTeamsSet.clear();
        eliminatedPlayersSet.clear();
        teamSurviveTimeMap.clear();
        playerSurviveTimeMap.clear();
    }
}
