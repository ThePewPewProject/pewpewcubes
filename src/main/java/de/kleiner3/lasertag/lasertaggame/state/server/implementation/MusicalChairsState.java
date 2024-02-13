package de.kleiner3.lasertag.lasertaggame.state.server.implementation;

import de.kleiner3.lasertag.lasertaggame.state.server.IMusicalChairsState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of IMusicalChairsState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class MusicalChairsState implements IMusicalChairsState {

    /**
     * Map mapping every eliminated team to their survive time
     *     key: The team
     *     value: The time in seconds the team got eliminated
     */
    private final Map<TeamDto, Long> teamSurviveTimeMap = new ConcurrentHashMap<>();

    /**
     * Map mapping every player's uuid to the overall score they achieved
     *     key: The players uuid
     *     value: The overall score the player achieved
     */
    private final Map<UUID, Long> playerOverallScoreMap = new ConcurrentHashMap<>();

    @Override
    public void setTeamSurviveTime(TeamDto team, long surviveTime) {
        teamSurviveTimeMap.put(team, surviveTime);
    }

    @Override
    public Long getTeamSurviveTime(TeamDto teamDto) {
        return teamSurviveTimeMap.get(teamDto);
    }

    @Override
    public void setPlayerOverallScore(UUID playerUuid, long newScore) {
        playerOverallScoreMap.put(playerUuid, newScore);
    }

    @Override
    public long getPlayerOverallScore(UUID playerUuid) {
        return Optional.ofNullable(playerOverallScoreMap.get(playerUuid)).orElse(0L);
    }

    @Override
    public void reset() {

        teamSurviveTimeMap.clear();
        playerOverallScoreMap.clear();
    }
}
