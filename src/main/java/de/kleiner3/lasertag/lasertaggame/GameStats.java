package de.kleiner3.lasertag.lasertaggame;

import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.util.Tuple;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameStats {
    public ArrayList<Tuple<Colors.Color, Integer>> teamScores = new ArrayList<>(6);
    public ArrayList<Tuple<PlayerEntity, Integer>> playerScores = new ArrayList<>();
    public HashMap<Colors.Color, List<Tuple<PlayerEntity, Integer>>> teamPlayerScores = new HashMap<>();
}
