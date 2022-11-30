package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.dummy.PlayerDummy;
import de.kleiner3.lasertag.lasertaggame.ILasertagPlayer;
import de.kleiner3.lasertag.types.Colors;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatsCalculatorTest {

    @Test
    void calcStatsInOrder() {
        // Build game stats object
        var stats = new HashMap<Colors.Color, List<ILasertagPlayer>>();

        var players = new LinkedList<ILasertagPlayer>();
        players.add(new PlayerDummy("Erster", 100));
        players.add(new PlayerDummy("Zweiter", 80));
        players.add(new PlayerDummy("Dritter", 67));

        stats.put(new Colors.Color("Red", 255, 0, 0, null), players);

        players = new LinkedList<>();
        players.add(new PlayerDummy("Erster", 50));
        players.add(new PlayerDummy("Zweiter", 40));
        players.add(new PlayerDummy("Dritter", 5));

        stats.put(new Colors.Color("Green", 255, 0, 0, null), players);

        var calculator = new StatsCalculator(stats);
        calculator.calcStats();

        var calcedStats = calculator.getLastGamesStats();

        assertEquals(247, calcedStats.teamScores.get(0).y);
        assertEquals(100, calcedStats.playerScores.get(0).y);
        assertEquals(80, calcedStats.playerScores.get(1).y);
        assertEquals(67, calcedStats.playerScores.get(2).y);

        assertEquals(95, calcedStats.teamScores.get(1).y);
        assertEquals(50, calcedStats.playerScores.get(3).y);
        assertEquals(40, calcedStats.playerScores.get(4).y);
        assertEquals(5, calcedStats.playerScores.get(5).y);
    }

    @Test
    void calcStatsOutOfOrder() {
        // Build game stats object
        var stats = new HashMap<Colors.Color, List<ILasertagPlayer>>();

        var players = new LinkedList<ILasertagPlayer>();
        players.add(new PlayerDummy("Dritter", 5));
        players.add(new PlayerDummy("Erster", 50));
        players.add(new PlayerDummy("Zweiter", 40));

        stats.put(new Colors.Color("Green", 255, 0, 0, null), players);

        players = new LinkedList<>();
        players.add(new PlayerDummy("Zweiter", 80));
        players.add(new PlayerDummy("Erster", 100));
        players.add(new PlayerDummy("Dritter", 67));

        stats.put(new Colors.Color("Red", 255, 0, 0, null), players);

        var calculator = new StatsCalculator(stats);
        calculator.calcStats();

        var calcedStats = calculator.getLastGamesStats();

        assertEquals(247, calcedStats.teamScores.get(0).y);
        assertEquals(100, calcedStats.playerScores.get(0).y);
        assertEquals(80, calcedStats.playerScores.get(1).y);
        assertEquals(67, calcedStats.playerScores.get(2).y);

        assertEquals(95, calcedStats.teamScores.get(1).y);
        assertEquals(50, calcedStats.playerScores.get(3).y);
        assertEquals(40, calcedStats.playerScores.get(4).y);
        assertEquals(5, calcedStats.playerScores.get(5).y);
    }

    @Test
    void calcStatsSingleEntry() {
        // Build game stats object
        var stats = new HashMap<Colors.Color, List<ILasertagPlayer>>();

        var players = new LinkedList<ILasertagPlayer>();
        players.add(new PlayerDummy("Erster", 50));

        stats.put(new Colors.Color("Green", 255, 0, 0, null), players);

        var calculator = new StatsCalculator(stats);
        calculator.calcStats();

        var calcedStats = calculator.getLastGamesStats();

        assertEquals(50, calcedStats.teamScores.get(0).y);
        assertEquals(50, calcedStats.playerScores.get(0).y);
        assertEquals(50, calcedStats.teamPlayerScores.get("Green").get(0).y);
    }

    @Test
    void calcStatsNoEntry() {
        // Build game stats object
        var stats = new HashMap<Colors.Color, List<ILasertagPlayer>>();

        var calculator = new StatsCalculator(stats);
        calculator.calcStats();

        var calcedStats = calculator.getLastGamesStats();

        assertNotEquals(null, calcedStats);
    }
}