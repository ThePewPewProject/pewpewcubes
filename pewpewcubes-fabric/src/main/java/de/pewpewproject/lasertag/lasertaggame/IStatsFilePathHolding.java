package de.pewpewproject.lasertag.lasertaggame;

import java.util.Optional;

/**
 * Interface for an object capable of holding a path to the statistics file
 *
 * @author Étienne Muser
 */
public interface IStatsFilePathHolding {

    /**
     * Sets the path to the statistics file
     *
     * @param newPath The new path as a string
     */
    default void setStatsFilePath(String newPath) {}

    /**
     * Gets the path to the last games stats
     *
     * @return Optional containing the path as a string. Optional.empty if there was no last game on this World.
     */
    default Optional<String> getStatsFilePath() {
        return Optional.empty();
    }
}
