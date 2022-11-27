package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.util.Tuple;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Class to calculate the lasertag game stats
 *
 * @author Étienne Muser
 */
public class StatsCalculator {
    private final HashMap<Colors.Color, List<PlayerEntity>> teamMap;

    private GameStats lastGamesStats;

    public StatsCalculator(HashMap<Colors.Color, List<PlayerEntity>> teamMap) {
        this.teamMap = teamMap;
    }

    public void calcStats() {
        lastGamesStats = new GameStats();
        for (Colors.Color teamColor : teamMap.keySet()) {
            var team = teamMap.get(teamColor);
            int teamScore = 0;

            var playersOfThisTeam = new ArrayList<Tuple<String, Integer>>();
            for (PlayerEntity player : team) {
                int playerScore = player.getLasertagScore();
                var playerScoreTuple = new Tuple<>(player.getDisplayName().getString(), playerScore);
                teamScore += playerScore;
                lastGamesStats.playerScores.add(playerScoreTuple);
                playersOfThisTeam.add(playerScoreTuple);
            }

            if (playersOfThisTeam.size() > 0) {
                lastGamesStats.teamPlayerScores.put(teamColor.getName(), playersOfThisTeam);
                lastGamesStats.teamScores.add(new Tuple<>(teamColor.getName(), teamScore));
            }
        }

        // Sort stats
        lastGamesStats.teamScores.sort(Comparator.comparingInt(t -> t.y));
        lastGamesStats.playerScores.sort(Comparator.comparingInt(t -> t.y));
        for (var team : lastGamesStats.teamPlayerScores.values()) {
            team.sort(Comparator.comparingInt(t -> t.y));
        }
    }

    public GameStats getLastGamesStats() {
        return lastGamesStats;
    }
}
