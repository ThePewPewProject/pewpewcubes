package de.kleiner3.lasertag.lasertaggame;

import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.util.Tuple;
import net.minecraft.entity.player.PlayerEntity;

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
    public ArrayList<Tuple<Colors.Color, Integer>> teamScores = new ArrayList<>(6);

    /**
     * Map every player to his score ordered by score
     */
    public ArrayList<Tuple<PlayerEntity, Integer>> playerScores = new ArrayList<>();

    /**
     * Map every team to the teams players and their scores ordered by scores inside the teams
     */
    public HashMap<Colors.Color, List<Tuple<PlayerEntity, Integer>>> teamPlayerScores = new HashMap<>();
}
