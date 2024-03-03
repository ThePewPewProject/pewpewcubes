package de.pewpewproject.lasertag.lasertaggame.state.management.server;

import de.pewpewproject.lasertag.worldgen.chunkgen.type.ArenaType;

/**
 * Interface for a server music manager
 *
 * @author Étienne Muser
 */
public interface IMusicManager {

    /**
     * Play the intro of an arena type
     *
     * @param arenaType The arena type to play the intro of
     */
    void playIntro(ArenaType arenaType);

    /**
     * Tick event (a minute passed)
     *
     * @param arenaType    The arena type
     * @param isLastMinute Flag to indicate this is the last minute of the game
     */
    void tick(ArenaType arenaType, boolean isLastMinute);

    /**
     * Play the outro of an arena type
     *
     * @param arenaType The arena type to play the outro of
     */
    void playOutro(ArenaType arenaType);

    /**
     * Stop all currently playing music
     */
    void stopMusic();

    /**
     * Reset the state
     */
    void reset();
}
