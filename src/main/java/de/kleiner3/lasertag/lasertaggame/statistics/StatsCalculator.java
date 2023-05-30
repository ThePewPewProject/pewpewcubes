package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.players.LasertagPlayerNameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class to calculate the lasertag game stats
 *
 * @author Étienne Muser
 */
public class StatsCalculator {

    public static GameStats calcStats(LasertagPlayerNameManager playerManager) {

        var stats = new GameStats();

        var scoreManager = LasertagGameManager.getInstance().getScoreManager();

        for (var team : LasertagGameManager.getInstance().getTeamManager().getTeamMap().entrySet()) {

            var teamDto = team.getKey();

            // Skip spectators
            if (teamDto.equals(TeamConfigManager.SPECTATORS)) {
                continue;
            }

            var teamScore = 0L;

            var playersOfThisTeam = new ArrayList<Tuple<String, Long>>();
            for (var playerUuid : team.getValue()) {

                var score = scoreManager.getScore(playerUuid);
                var playerScoreTuple = new Tuple<>(playerManager.getPlayerUsername(playerUuid), score);

                teamScore += score;
                stats.playerScores.add(playerScoreTuple);
                playersOfThisTeam.add(playerScoreTuple);
            }

            if (playersOfThisTeam.size() > 0) {
                stats.teamPlayerScores.put(teamDto.name(), playersOfThisTeam);
                stats.teamScores.add(new Tuple<>(teamDto.name(), teamScore));
            }
        }

        // Sort stats
        stats.teamScores.sort(Comparator.<Tuple<String, Long>>comparingLong(t -> t.y()).reversed());
        stats.playerScores.sort(Comparator.<Tuple<String, Long>>comparingLong(t -> t.y()).reversed());
        for (var team : stats.teamPlayerScores.values()) {
            team.sort(Comparator.<Tuple<String, Long>>comparingLong(t -> t.y()).reversed());
        }

        return stats;
    }
}
