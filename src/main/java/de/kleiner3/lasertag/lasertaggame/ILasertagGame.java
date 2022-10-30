package de.kleiner3.lasertag.lasertaggame;

import java.util.LinkedList;
import java.util.List;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Interface to provide methods for a lasertag game
 *
 * @author Étienne Muser
 */
public interface ILasertagGame {

    /**
     * Start the lasertag game
     */
    default public void startGame(boolean scanSpawnpoints) {
    }

    /**
     * Get all players currently assigned to the team of the given color
     *
     * @param color
     * @return
     */
    default public List<PlayerEntity> getPlayersOfTeam(Colors.Color color) {
        return new LinkedList<PlayerEntity>();
    }

    /**
     * Add a player to the team of the given color
     *
     * @param color
     * @param player
     */
    default public void playerJoinTeam(Colors.Color color, PlayerEntity player) {
    }

    /**
     * Remove a player from the team of the given color
     *
     * @param color
     * @param player
     */
    default public void playerLeaveTeam(Colors.Color color, PlayerEntity player) {
    }

    /**
     * Called when a player scored points
     *
     * @param player
     * @param score
     */
    default public void onPlayerScored(PlayerEntity player, int score) {
    }

    /**
     * Force remove a player from his team
     *
     * @param player
     */
    default public void playerLeaveHisTeam(PlayerEntity player) {
    }

    /**
     * Seraches the world for the spawnpoint blocks and caches them
     */
    default public void initSpawnpointCache() {
    }

    /**
     * @return True if the game is running
     */
    default public boolean isRunning() {
        return false;
    }

    default public void registerLasertarget(LaserTargetBlockEntity target) {
    }
}
