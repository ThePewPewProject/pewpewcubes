package de.kleiner3.lasertag.lasertaggame.state.management.server;

import de.kleiner3.lasertag.block.entity.LasertagCustomBlockTickable;

/**
 * Interface for a server block tick manager
 *
 * @author Étienne Muser
 */
public interface IBlockTickManager {

    /**
     * Register a block entity ticker
     *
     * @param ticker The ticker to register
     */
    void registerTicker(LasertagCustomBlockTickable ticker);

    /**
     * Clear the registered block entity tickers
     */
    void clear();

    /**
     * Tick all registered block entity tickers
     */
    void tick();
}
