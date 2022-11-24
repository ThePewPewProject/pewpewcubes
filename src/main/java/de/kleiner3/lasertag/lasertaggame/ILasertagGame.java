package de.kleiner3.lasertag.lasertaggame;

import java.util.LinkedList;
import java.util.List;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.types.Colors;
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
    }

    /**
     * Get all players currently assigned to the team of the given color
     *
     * @param color
     * @return
     */
    default List<PlayerEntity> getPlayersOfTeam(Colors.Color color) {
        return new LinkedList<>();
    }

    /**
     * Add a player to the team of the given color
     *
     * @param color
     * @param player
     */
    default void playerJoinTeam(Colors.Color color, PlayerEntity player) {
    }

    /**
     * Remove a player from the team of the given color
     *
     * @param color
     * @param player
     */
    default void playerLeaveTeam(Colors.Color color, PlayerEntity player) {
    }

    /**
     * Called when a player scored points
     *
     * @param player
     * @param score
     */
    default void onPlayerScored(PlayerEntity player, int score) {
    }

    /**
     * Force remove a player from his team
     *
     * @param player
     */
    default void playerLeaveHisTeam(PlayerEntity player) {
    }

    /**
     * Seraches the world for the spawnpoint blocks and caches them
     */
    default void initSpawnpointCache() {
    }

    /**
     * @return True if the game is running
     */
    default boolean isRunning() {
        return false;
    }

    /**
     * Register a lasertarget to be reset after game
     * @param target
     */
    default void registerLasertarget(LaserTargetBlockEntity target) {
    }

    /**
     * Synchronize the teams and scores to a player
     * @param player
     */
    default void syncTeamsAndScoresToPlayer(ServerPlayerEntity player) {
    }
}
