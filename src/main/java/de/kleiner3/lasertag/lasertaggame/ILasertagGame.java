package de.kleiner3.lasertag.lasertaggame;

import java.util.LinkedList;
import java.util.List;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamDto;
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
     * Get all players currently assigned to the given team
     *
     * @param teamDto The team to get the players of
     * @return The list of all players in this team
     */
    default List<PlayerEntity> getPlayersOfTeam(TeamDto teamDto) {
        return new LinkedList<>();
    }

    /**
     * Add a player to the given team
     *
     * @param teamDto The team to join
     * @param player The player to join the team
     */
    default void playerJoinTeam(TeamDto teamDto, PlayerEntity player) {
    }

    /**
     * Called when a player scored points
     *
     * @param player The player who scored
     * @param score The score he scored
     */
    default void onPlayerScored(PlayerEntity player, int score) {
    }

    /**
     * Force remove a player from his team
     *
     * @param player The player to leave his team
     */
    default void playerLeaveHisTeam(PlayerEntity player) {
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
    }

    /**
     * Synchronize the teams and scores to a player
     * @param player The player to synchronize to
     */
    default void syncTeamsAndScoresToPlayer(ServerPlayerEntity player) {
    }
}
