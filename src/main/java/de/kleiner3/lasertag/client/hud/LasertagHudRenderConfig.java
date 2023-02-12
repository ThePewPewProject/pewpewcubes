package de.kleiner3.lasertag.client.hud;

import de.kleiner3.lasertag.types.TeamConfigManager;
import de.kleiner3.lasertag.types.TeamDto;
import de.kleiner3.lasertag.util.Tuple;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class to hold all lasertag HUD data
 *
 * @author Étienne Muser
 */
public class LasertagHudRenderConfig {
    public boolean shouldRenderNameTags = true;
    /**
     * The time in seconds that has already elapsed
     */
    public long gameTime = 0;
    public ScheduledExecutorService gameTimer;
    public final Object gameTimerLock = new Object();

    /**
     * Simplified team map to map players and their score to their teams
     */
    public HashMap<String, LinkedList<Tuple<String, Integer>>> teamMap = new HashMap<>();

    public double progress = 0.0;

    public int startingIn = -1;

    // The logical size of the window
    public int width;
    public int wMid;
    public int height;
    public int hMid;

    // Constants
    public static final int progressBarWidth = 100;
    public static final int numTeams = TeamConfigManager.teamConfig.size();
    public static final int boxColor = 0x88000000;
    public static final int startY = 10;
    public static final int boxHeight = 65;
    public static final int boxWidth = 85;
    public static final int margin = 20;
    public static final int textPadding = 1;
    public static final int textHeight = 9;

    LasertagHudRenderConfig() {
        for (TeamDto t : TeamConfigManager.teamConfig.values()) {
            teamMap.put(t.name(), new LinkedList<>());
        }
    }
}
