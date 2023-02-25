package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.dummy.DummyWebResourceManager;
import de.kleiner3.lasertag.common.types.Tuple;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebStatisticsVisualizerTest {

    @Test
    void buildTest() {
        var stats = new GameStats();

        stats.playerScores.add(new Tuple<>("Der_fbs", 700L));
        stats.playerScores.add(new Tuple<>("KEV", 600L));
        stats.playerScores.add(new Tuple<>("SolAstrum", 500L));
        stats.playerScores.add(new Tuple<>("Sogeking_1024", 400L));
        stats.playerScores.add(new Tuple<>("fibs__", 300L));
        stats.playerScores.add(new Tuple<>("RaffiHolzi", 200L));

        var list = new LinkedList<Tuple<String, Long>>();
        list.add(new Tuple<>("Der_fbs", 700L));
        list.add(new Tuple<>("SolAstrum", 500L));
        stats.teamPlayerScores.put("Teal", list);

        list = new LinkedList<>();
        list.add(new Tuple<>("KEV", 600L));
        list.add(new Tuple<>("fibs__", 300L));
        stats.teamPlayerScores.put("Orange", list);

        list = new LinkedList<>();
        list.add(new Tuple<>("Sogeking_1024", 400L));
        list.add(new Tuple<>("RaffiHolzi", 200L));
        stats.teamPlayerScores.put("Pink", list);

        stats.teamScores.add(new Tuple<>("Teal", 1300L));
        stats.teamScores.add(new Tuple<>("Orange", 900L));
        stats.teamScores.add(new Tuple<>("Pink", 600L));

        var result = WebStatisticsVisualizer.build(stats, new DummyWebResourceManager());

        assertNotNull(result);
    }
}