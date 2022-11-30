package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.dummy.DummyWebResourceManager;
import de.kleiner3.lasertag.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebStatisticsVisualizerTest {

    @Test
    void buildTest() {
        var stats = new GameStats();

        stats.playerScores.add(new Tuple<>("Der_fbs", 700));
        stats.playerScores.add(new Tuple<>("KEV", 600));
        stats.playerScores.add(new Tuple<>("SolAstrum", 500));
        stats.playerScores.add(new Tuple<>("Sogeking_1024", 400));
        stats.playerScores.add(new Tuple<>("fibs__", 300));
        stats.playerScores.add(new Tuple<>("RaffiHolzi", 200));

        var list = new LinkedList<Tuple<String, Integer>>();
        list.add(new Tuple<>("Der_fbs", 700));
        list.add(new Tuple<>("SolAstrum", 500));
        stats.teamPlayerScores.put("Teal", list);

        list = new LinkedList<>();
        list.add(new Tuple<>("KEV", 600));
        list.add(new Tuple<>("fibs__", 300));
        stats.teamPlayerScores.put("Orange", list);

        list = new LinkedList<>();
        list.add(new Tuple<>("Sogeking_1024", 400));
        list.add(new Tuple<>("RaffiHolzi", 200));
        stats.teamPlayerScores.put("Pink", list);

        stats.teamScores.add(new Tuple<>("Teal", 1300));
        stats.teamScores.add(new Tuple<>("Orange", 900));
        stats.teamScores.add(new Tuple<>("Pink", 600));

        var result = WebStatisticsVisualizer.build(stats, new DummyWebResourceManager());

        assertNotNull(result);
    }
}