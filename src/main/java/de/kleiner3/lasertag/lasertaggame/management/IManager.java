package de.kleiner3.lasertag.lasertaggame.management;

/**
 * Interface for a lasertag game manager. Everything that should be synchronized between server and clients should
 * be inside a manager
 *
 * @author Étienne Muser
 */
public interface IManager {
    void dispose();
}
