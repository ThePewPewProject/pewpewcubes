package de.pewpewproject.lasertag.lasertaggame.statistics;

import de.pewpewproject.lasertag.common.types.Tuple;

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
     * The game duration in seconds
     */
    public long gameDurationSeconds = 0L;

    /**
     * Map every team to the teams combined score ordered by score
     */
    public final ArrayList<Tuple<String, String>> teamScores = new ArrayList<>(6);

    /**
     * Map every player to his score ordered by score
     */
    public final ArrayList<Tuple<String, String>> playerScores = new ArrayList<>();

    /**
     * Map every team to the teams players and their scores ordered by scores inside the teams
     */
    public final HashMap<String, List<Tuple<String, String>>> teamPlayerScores = new HashMap<>();
}
