package de.pewpewproject.lasertag.lasertaggame.state.management.server;

import de.pewpewproject.lasertag.block.entity.LaserTargetBlockEntity;
import de.pewpewproject.lasertag.lasertaggame.ITickable;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.*;
import de.pewpewproject.lasertag.lasertaggame.state.synced.ISyncedState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for a server lasertag manager
 *
 * @author Étienne Muser
 */
public interface IServerLasertagManager extends ITickable {

    /**
     * Start the lasertag game
     *
     * @param scanSpawnpoints Flag to indicate that the spawnpoints should be rescanned
     * @return Completable future returning an optional containing the reasons the start was aborted.
     * Optional.empty if the start wasn't aborted.
     */
    CompletableFuture<Optional<String>> startGame(boolean scanSpawnpoints);

    /**
     * Stop the running lasertag game
     *
     * @return True if the game was stopped. False if there was no game running.
     */
    boolean stopLasertagGame();

    /**
     * Get if a game is currently running
     *
     * @return True if a game is currently running. Otherwise, false.
     */
    boolean isGameRunning();

    /**
     * Get if the pre game count down is over
     *
     * @return True if the pre game count down is over and a game is running. Otherwise, false.
     */
    boolean hasPreGamePassed();

    /**
     * A player hit another player
     *
     * @param shooter The player who fired the weapon
     * @param target  The player who got hit
     */
    void playerHitPlayer(ServerPlayerEntity shooter, ServerPlayerEntity target);

    /**
     * A player hit another player
     *
     * @param shooterUuid The uuid of the player who fired the weapon
     * @param targetUuid  The uuid of the player who got hit
     */
    void playerHitPlayer(UUID shooterUuid, UUID targetUuid);

    /**
     * A player hit a lasertarget
     *
     * @param shooter The player who fired the weapon
     * @param target  The lasertarget which got hit
     */
    void playerHitLasertarget(ServerPlayerEntity shooter, LaserTargetBlockEntity target);

    /**
     * A player hit a lasertarget
     *
     * @param shooterUuid The uuid of the player who fired the weapon
     * @param ragetPos    The block position of the lasertarget which got hit
     */
    void playerHitLasertarget(UUID shooterUuid, BlockPos ragetPos);

    /**
     * Dispose hte lasertag manager and all sub-managers
     */
    void dispose();

    /**
     * Synchronize the current synced state to the client
     *
     * @param client The client to sync to
     */
    void syncStateToClient(ServerPlayerEntity client);

    ISyncedState getSyncedState();

    IArenaManager getArenaManager();

    IBlockTickManager getBlockTickManager();

    ISettingsPresetsManager getSettingsPresetsManager();

    ISpawnpointManager getSpawnpointManager();

    IActivationManager getActivationManager();

    ICaptureTheFlagManager getCaptureTheFlagManager();

    IGameModeManager getGameModeManager();

    IScoreManager getScoreManager();

    ISettingsManager getSettingsManager();

    ISettingsPresetsNameManager getSettingsPresetsNameManager();

    ITeamsManager getTeamsManager();

    IMusicalChairsManager getMusicalChairsManager();

    IEliminationManager getEliminationManager();

    ILasertargetsManager getLasertargetsManager();

    IStartGamePermissionManager getStartGamePermissionManager();
}
