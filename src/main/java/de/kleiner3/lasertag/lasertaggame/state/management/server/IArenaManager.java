package de.kleiner3.lasertag.lasertaggame.state.management.server;

import de.kleiner3.lasertag.worldgen.chunkgen.type.ArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ProceduralArenaType;

/**
 * Interface for a server arena manager
 *
 * @author Étienne Muser
 */
public interface IArenaManager {

    /**
     * Load an arena
     *
     * @param newArenaType The new arena type
     * @param newProceduralArenaType The new procedural arena type
     * @return True if the arena could be loaded. Otherwise, false.
     */
    boolean loadArena(ArenaType newArenaType, ProceduralArenaType newProceduralArenaType);

    /**
     * Check if an arena is currently loading
     *
     * @return True if an arena is currently loading. Otherwise, false.
     */
    boolean isLoading();
}
