package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.common.types.ScoreHolding;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.players.LasertagPlayerNameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Class to calculate the lasertag game stats
 *
 * @author Étienne Muser
 */
public class StatsCalculator {

    public static GameStats calcStats(LasertagPlayerNameManager playerManager) {

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var gameMode = gameManager.getGameModeManager().getGameMode();

        // Init lists for sorting
        var playerScores = new ArrayList<Tuple<String, ScoreHolding>>();
        var teamScores = new ArrayList<Tuple<String, ScoreHolding>>();
        var teamPlayerScores = new HashMap<String, ArrayList<Tuple<String, ScoreHolding>>>();

        for (var team : gameManager.getTeamManager().getTeamMap().entrySet()) {

            var teamDto = team.getKey();

            // Skip spectators
            if (teamDto.equals(TeamConfigManager.SPECTATORS)) {
                continue;
            }

            var playersOfThisTeam = new ArrayList<Tuple<String, ScoreHolding>>();
            for (var playerUuid : team.getValue()) {

                var score = gameMode.getPlayerFinalScore(playerUuid);
                var playerScoreTuple = new Tuple<>(playerManager.getPlayerUsername(playerUuid), score);

                playerScores.add(playerScoreTuple);
                playersOfThisTeam.add(playerScoreTuple);
            }

            var teamScore = gameMode.getTeamFinalScore(teamDto);

            if (!playersOfThisTeam.isEmpty()) {
                teamPlayerScores.put(teamDto.name(), playersOfThisTeam);
                teamScores.add(new Tuple<>(teamDto.name(), teamScore));
            }
        }

        // Sort stats
        teamScores.sort(Comparator.<Tuple<String, ScoreHolding>, ScoreHolding>comparing(Tuple::y).reversed());
        playerScores.sort(Comparator.<Tuple<String, ScoreHolding>, ScoreHolding>comparing(Tuple::y).reversed());
        for (var team : teamPlayerScores.values()) {
            team.sort(Comparator.<Tuple<String, ScoreHolding>, ScoreHolding>comparing(Tuple::y).reversed());
        }

        // Create stats object
        var stats = new GameStats();

        // Convert team scores
        stats.teamScores.addAll(teamScores.stream().map(v -> new Tuple<>(v.x(), v.y().getValueString())).toList());

        // Convert player scores
        stats.playerScores.addAll(playerScores.stream().map(v -> new Tuple<>(v.x(), v.y().getValueString())).toList());

        // Convert team player scores
        for (var entry : teamPlayerScores.entrySet()) {
            stats.teamPlayerScores.put(entry.getKey(), new ArrayList<>(entry.getValue().stream().map(v -> new Tuple<>(v.x(), v.y().getValueString())).toList()));
        }

        return stats;
    }
}
