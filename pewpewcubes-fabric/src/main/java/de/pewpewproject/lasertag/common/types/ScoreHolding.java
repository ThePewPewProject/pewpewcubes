package de.pewpewproject.lasertag.common.types;

/**
 * Interface for objects that are able to hold lasertag scores
 *
 * @author Étienne Muser
 */
public interface ScoreHolding extends Comparable<ScoreHolding> {
    /**
     * Get the value of this object as a string
     *
     * @return The value as a string
     */
    String getValueString();
}
