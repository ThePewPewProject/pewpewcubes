package de.kleiner3.lasertag.lasertaggame;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Interface to provide methods for a lasertag game
 *
 * @author Étienne Muser
 */
public interface ILasertagGame {

    /**
     * Start the lasertag game
     */
    default void startGame(boolean scanSpawnpoints) {
        // Default empty
    }

    /**
     * Stops the running lasertag game
     * @return False if there was no game running. Otherwise, true.
     */
    default boolean stopLasertagGame() {
        return false;
    }

    /**
     * Add a player to the given team
     *
     * @param teamDto The team to join
     * @param player The player to join the team
     */
    default void playerJoinTeam(TeamDto teamDto, PlayerEntity player) {
        // Default empty
    }

    /**
     * Called when a player scored points
     *
     * @param player The player who scored
     * @param score The score he scored
     */
    default void onPlayerScored(PlayerEntity player, long score) {
        // Default empty
    }

    /**
     * Force remove a player from his team
     *
     * @param player The player to leave his team
     */
    default void playerLeaveHisTeam(PlayerEntity player) {
        // Default empty
    }

    /**
     * @return True if the game is running
     */
    default boolean isLasertagGameRunning() {
        return false;
    }

    /**
     * Register a lasertarget to be reset after game
     * @param target The lasertarget block entity to register
     */
    default void registerLasertarget(LaserTargetBlockEntity target) {
        // Default empty
    }

    /**
     * Synchronize the teams and scores to a player
     * @param player The player to synchronize to
     */
    default void syncTeamsAndScoresToPlayer(ServerPlayerEntity player) {
        // Default empty
    }

    /**
     * Notifies every player of this world about a team or score update
     */
    default void notifyPlayersAboutUpdate() {
        // Default empty
    }

    /**
     * Checks whether a palyer is in a team or not
     * @param player
     * @return If the player is in a team
     */
    default boolean isPlayerInTeam(ServerPlayerEntity player) {
        return false;
    }

    /**
     * Disposes the game. Stops all running timers etc.
     */
    default void dispose() {
        // Default empty
    }

    /**
     * Gets the simplified team map
     * @return
     */
    default HashMap<String, List<Tuple<String, Long>>> getSimplifiedTeamMap() {
        return null;
    }

    /**
     * Gets the team of the player or null if he is in no team
     * @param playerUuid The uuid of the player
     * @return
     */
    default TeamDto getTeamOfPlayer(UUID playerUuid) {
        return null;
    }
}
