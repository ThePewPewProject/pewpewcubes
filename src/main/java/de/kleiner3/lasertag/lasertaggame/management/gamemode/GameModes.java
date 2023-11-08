package de.kleiner3.lasertag.lasertaggame.management.gamemode;

import de.kleiner3.lasertag.lasertaggame.management.gamemode.implementation.CaptureTheFlagGameMode;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.implementation.PointHunterGameMode;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Class holding all game modes
 *
 * @author Étienne Muser
 */
public class GameModes {

    public static final GameMode POINT_HUNTER_GAME_MODE = new PointHunterGameMode();
    public static final GameMode CAPTURE_THE_FLAG_GAME_MODE = new CaptureTheFlagGameMode();

    public static final Map<String, GameMode> GAME_MODES = Map.ofEntries(
            entry(POINT_HUNTER_GAME_MODE.getTranslatableName(), POINT_HUNTER_GAME_MODE),
            entry(CAPTURE_THE_FLAG_GAME_MODE.getTranslatableName(), CAPTURE_THE_FLAG_GAME_MODE)
    );
}
