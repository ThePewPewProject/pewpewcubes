package de.kleiner3.lasertag.lasertaggame.statistics;

import de.kleiner3.lasertag.common.types.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * DTO for a games stats
 *
 * @author Étienne Muser
 */
public class GameStats {
    /**
     * Map every team to the teams combined score ordered by score
     */
    public final ArrayList<Tuple<String, Long>> teamScores = new ArrayList<>(6);

    /**
     * Map every player to his score ordered by score
     */
    public final ArrayList<Tuple<String, Long>> playerScores = new ArrayList<>();

    /**
     * Map every team to the teams players and their scores ordered by scores inside the teams
     */
    public final HashMap<String, List<Tuple<String, Long>>> teamPlayerScores = new HashMap<>();
}
