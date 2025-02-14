package de.pewpewproject.lasertag.lasertaggame.state.synced;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Interface for a player names state.
 * Resembles the state of what player uuid has what username.
 *
 * @author Étienne Muser
 */
public interface IPlayerNamesState {

    /**
     * Save an username to an uuid
     *
     * @param playerUuid     The uuid of the player to save the username of
     * @param playerUsername The username to save
     */
    void savePlayerUsername(UUID playerUuid, String playerUsername);

    /**
     * Get the username of a player
     *
     * @param playerUuid The uuid of the player to get the username from
     * @return A string containing the players username
     */
    String getPlayerUsername(UUID playerUuid);

    /**
     * Execute a callback on every player registered
     *
     * @param consumer The callback to execute. Takes the uuid of the player.
     */
    void forEachPlayer(Consumer<UUID> consumer);
}
