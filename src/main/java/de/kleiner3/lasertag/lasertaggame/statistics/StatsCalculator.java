package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamDto;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamMap;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class to calculate the lasertag game stats
 *
 * @author Étienne Muser
 */
public class StatsCalculator {
    private final TeamMap teamMap;

    private GameStats lastGamesStats;

    public StatsCalculator(TeamMap teamMap) {
        this.teamMap = teamMap;
    }

    public void calcStats() {
        lastGamesStats = new GameStats();
        for (TeamDto teamDto : teamMap.keySet()) {
            var team = teamMap.get(teamDto);
            int teamScore = 0;

            var playersOfThisTeam = new ArrayList<Tuple<String, Integer>>();
            for (var player : team) {
                int playerScore = player.getLasertagScore();
                var playerScoreTuple = new Tuple<>(player.getLasertagUsername(), playerScore);
                teamScore += playerScore;
                lastGamesStats.playerScores.add(playerScoreTuple);
                playersOfThisTeam.add(playerScoreTuple);
            }

            if (playersOfThisTeam.size() > 0) {
                lastGamesStats.teamPlayerScores.put(teamDto.name(), playersOfThisTeam);
                lastGamesStats.teamScores.add(new Tuple<>(teamDto.name(), teamScore));
            }
        }

        // Sort stats
        lastGamesStats.teamScores.sort(Comparator.<Tuple<String, Integer>>comparingInt(t -> t.y()).reversed());
        lastGamesStats.playerScores.sort(Comparator.<Tuple<String, Integer>>comparingInt(t -> t.y()).reversed());
        for (var team : lastGamesStats.teamPlayerScores.values()) {
            team.sort(Comparator.<Tuple<String, Integer>>comparingInt(t -> t.y()).reversed());
        }
    }

    public GameStats getLastGamesStats() {
        return lastGamesStats;
    }
}
