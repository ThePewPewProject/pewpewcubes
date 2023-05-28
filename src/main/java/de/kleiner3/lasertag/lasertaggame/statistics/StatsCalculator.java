package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class to calculate the lasertag game stats
 *
 * @author Étienne Muser
 */
public class StatsCalculator {
    private final MinecraftServer server;

    private GameStats lastGamesStats;

    public StatsCalculator(MinecraftServer server) {
        this.server = server;
    }

    public void calcStats() {
        var teamMap = server.getSimplifiedTeamMap();

        lastGamesStats = new GameStats();
        for (TeamDto teamDto : LasertagGameManager.getInstance().getTeamManager().teamConfig.values()) {

            // Skip spectators
            if (teamDto.equals(TeamConfigManager.SPECTATORS)) {
                continue;
            }

            var team = teamMap.get(teamDto.name());
            var teamScore = 0L;

            var playersOfThisTeam = new ArrayList<Tuple<String, Long>>();
            for (var playerScoreTuple : team) {
                teamScore += playerScoreTuple.y();
                lastGamesStats.playerScores.add(playerScoreTuple);
                playersOfThisTeam.add(playerScoreTuple);
            }

            if (playersOfThisTeam.size() > 0) {
                lastGamesStats.teamPlayerScores.put(teamDto.name(), playersOfThisTeam);
                lastGamesStats.teamScores.add(new Tuple<>(teamDto.name(), teamScore));
            }
        }

        // Sort stats
        lastGamesStats.teamScores.sort(Comparator.<Tuple<String, Long>>comparingLong(t -> t.y()).reversed());
        lastGamesStats.playerScores.sort(Comparator.<Tuple<String, Long>>comparingLong(t -> t.y()).reversed());
        for (var team : lastGamesStats.teamPlayerScores.values()) {
            team.sort(Comparator.<Tuple<String, Long>>comparingLong(t -> t.y()).reversed());
        }
    }

    public GameStats getLastGamesStats() {
        return lastGamesStats;
    }
}
