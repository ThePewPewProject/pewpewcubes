package de.pewpewproject.lasertag.lasertaggame.statistics;

import de.pewpewproject.lasertag.common.types.ScoreHolding;
import de.pewpewproject.lasertag.common.types.Tuple;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.IServerLasertagManager;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.TeamsConfigState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Class to calculate the lasertag game stats
 *
 * @author Étienne Muser
 */
public class StatsCalculator {

    public static GameStats calcStats(IServerLasertagManager gameManager, long gameTime) {

        // Get the game mangers
        var syncedState = gameManager.getSyncedState();
        var teamsConfig = syncedState.getTeamsConfigState();
        var teamsManager = gameManager.getTeamsManager();
        var playerNamesState = syncedState.getPlayerNamesState();
        var gameModeManager = gameManager.getGameModeManager();

        var gameMode = gameModeManager.getGameMode();

        // Init lists for sorting
        var playerScores = new ArrayList<Tuple<String, ScoreHolding>>();
        var teamScores = new ArrayList<Tuple<String, ScoreHolding>>();
        var teamPlayerScores = new HashMap<String, ArrayList<Tuple<String, ScoreHolding>>>();

        for (var team : teamsConfig.getTeams()) {

            // Get the players of the team
            var playerUuids = teamsManager.getPlayersOfTeam(team);

            // Skip spectators
            if (team.equals(TeamsConfigState.SPECTATORS)) {
                continue;
            }

            // Skip empty teams
            if (playerUuids.isEmpty()) {
                continue;
            }

            var playersOfThisTeam = new ArrayList<Tuple<String, ScoreHolding>>();
            for (var playerUuid : playerUuids) {

                var score = gameMode.getPlayerFinalScore(playerUuid, gameManager);
                var playerScoreTuple = new Tuple<>(playerNamesState.getPlayerUsername(playerUuid), score);

                playerScores.add(playerScoreTuple);
                playersOfThisTeam.add(playerScoreTuple);
            }

            var teamScore = gameMode.getTeamFinalScore(team, gameManager);

            teamPlayerScores.put(team.name(), playersOfThisTeam);
            teamScores.add(new Tuple<>(team.name(), teamScore));
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

        // If is game mode with infinite time
        if (gameMode.hasInfiniteTime()) {
            // Safe the game duration
            stats.gameDurationSeconds = gameTime;
        }

        return stats;
    }
}
