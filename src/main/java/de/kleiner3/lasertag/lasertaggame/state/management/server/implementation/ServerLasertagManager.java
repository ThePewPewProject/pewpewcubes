package de.kleiner3.lasertag.lasertaggame.state.management.server.implementation;

import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.*;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.*;
import de.kleiner3.lasertag.lasertaggame.state.synced.ISyncedState;
import de.kleiner3.lasertag.lasertaggame.statistics.GameStats;
import de.kleiner3.lasertag.lasertaggame.statistics.StatsCalculator;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import de.kleiner3.lasertag.lasertaggame.team.serialize.TeamDtoSerializer;
import de.kleiner3.lasertag.lasertaggame.timing.GameTickTimerTask;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of IServerLasertagManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class ServerLasertagManager implements IServerLasertagManager {

    //region Necessary injected fields

    private final MinecraftServer server;

    //endregion

    //region Private fields

    /**
     * Flag to indicate whether the game is running.
     * True if the game is running. Otherwise, false.
     */
    private boolean isRunning;

    private ScheduledExecutorService gameTickTimer = null;

    /**
     * The synchronized state of the game.
     * This state must be synchronized with the clients.
     */
    private final ISyncedState syncedState;

    //endregion

    //region Server sub-managers

    private final IArenaManager arenaManager;
    private final IBlockTickManager blockTickManager;
    private final ILasertargetManager lasertargetManager;
    private final IMusicManager musicManager;
    private final ISettingsPresetsManager settingsPresetsManager;
    private final ISpawnpointManager spawnpointManager;
    private final IMusicalChairsManager musicalChairsManager;

    //endregion

    //region Synced sub-managers

    private final IActivationManager activationManager;
    private final ICaptureTheFlagManager captureTheFlagManager;
    private final IGameModeManager gameModeManager;
    private final IScoreManager scoreManager;
    private final ISettingsManager settingsManager;
    private final ISettingsPresetsNameManager settingsPresetsNameManager;
    private final ITeamsManager teamsManager;
    private final IUIStateManager uiStateManager;
    private final IEliminationManager eliminationManager;

    //endregion

    public ServerLasertagManager(MinecraftServer server,
                                 ISyncedState syncedState,
                                 IArenaManager arenaManager,
                                 IBlockTickManager blockTickManager,
                                 ILasertargetManager lasertargetManager,
                                 IMusicManager musicManager,
                                 ISettingsPresetsManager settingsPresetsManager,
                                 ISpawnpointManager spawnpointManager,
                                 IActivationManager activationManager,
                                 ICaptureTheFlagManager captureTheFlagManager,
                                 IGameModeManager gameModeManager,
                                 IScoreManager scoreManager,
                                 ISettingsManager settingsManager,
                                 ISettingsPresetsNameManager settingsPresetsNameManager,
                                 ITeamsManager teamsManager,
                                 IUIStateManager uiStateManager,
                                 IMusicalChairsManager musicalChairsManager,
                                 IEliminationManager eliminationManager) {

        this.server = server;
        this.syncedState = syncedState;
        this.eliminationManager = eliminationManager;
        isRunning = false;

        this.arenaManager = arenaManager;
        this.blockTickManager = blockTickManager;
        this.lasertargetManager = lasertargetManager;
        this.musicManager = musicManager;
        this.settingsPresetsManager = settingsPresetsManager;
        this.spawnpointManager = spawnpointManager;
        this.activationManager = activationManager;
        this.captureTheFlagManager = captureTheFlagManager;
        this.gameModeManager = gameModeManager;
        this.scoreManager = scoreManager;
        this.settingsManager = settingsManager;
        this.settingsPresetsNameManager = settingsPresetsNameManager;
        this.teamsManager = teamsManager;
        this.uiStateManager = uiStateManager;
        this.musicalChairsManager = musicalChairsManager;
    }

    //region Public methods

    /**
     * Start the lasertag game
     *
     * @return The reasons why the start game got aborted.
     */
    @Override
    public Optional<String> startGame(boolean scanSpawnpoints) {

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        var world = server.getOverworld();

        spawnpointManager.initSpawnpointCacheIfNecessary(world, scanSpawnpoints);

        // Check starting conditions
        var abortReasons = gameMode.checkStartingConditions(server);

        // If should abort
        if (abortReasons.isPresent()) {

            return abortReasons;
        }

        // Delegate to the game mode
        gameMode.sendPlayersToSpawnpoints(this.server);

        // Start game
        isRunning = true;
        syncedState.getUIState().isGameRunning = true;

        var preGameDelayTimer = ThreadUtil.createScheduledExecutor("server-lasertag-server-pregame-delay-timer-thread-%d");
        var preGameDelay = settingsManager.<Long>get(SettingDescription.PREGAME_DURATION);

        if (world.getChunkManager().getChunkGenerator() instanceof ArenaChunkGenerator arenaChunkGenerator) {
            musicManager.playIntro(arenaChunkGenerator.getConfig().getType());
        }

        preGameDelayTimer.schedule(() -> {

            // Delegate to game mode
            gameMode.onGameStart(this.server);

            // Start game tick timer
            gameTickTimer = ThreadUtil.createScheduledExecutor("server-lasertag-game-tick-timer-thread-%d");
            gameTickTimer.scheduleAtFixedRate(new GameTickTimerTask(this, gameModeManager, settingsManager), 0, 1, TimeUnit.SECONDS);

            // Stop the pre game delay timer
            preGameDelayTimer.shutdownNow();

        }, preGameDelay, TimeUnit.SECONDS);

        // Delegate to the game mode
        gameMode.onPreGameStart(this.server);

        // Notify players
        ServerEventSending.sendToEveryone(server, NetworkingConstants.GAME_STARTED, PacketByteBufs.empty());

        // If is on dedicated server
        if (server.isDedicated()) {

            // Start pregame count down timer
            uiStateManager.startPreGameCountdownTimer(settingsManager.<Long>get(SettingDescription.PREGAME_DURATION));
        }

        return Optional.empty();
    }

    /**
     * Stops the running lasertag game
     * @return False if there was no game running. Otherwise, true.
     */
    @Override
    public boolean stopLasertagGame() {

        // If there is no game running
        if (!isRunning) {
            return false;
        }

        // Stop the game
        this.dispose();
        this.lasertagGameOver();

        return true;
    }

    /**
     * @return True if the game is running
     */
    @Override
    public boolean isGameRunning() {
        return isRunning;
    }

    /**
     * This gets called when a player hit another player
     *
     * @param shooter The player who fired
     * @param target The player who got hit
     */
    @Override
    public void playerHitPlayer(ServerPlayerEntity shooter, ServerPlayerEntity target) {

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        gameMode.onPlayerHitPlayer(this.server, shooter, target);
    }

    /**
     * This gets called when a player hit another player
     *
     * @param shooterUuid The uuid of the player who fired
     * @param targetUuid The uuid of the player who got hit
     */
    @Override
    public void playerHitPlayer(UUID shooterUuid, UUID targetUuid) {

        var playerManager = server.getPlayerManager();

        var shooter = playerManager.getPlayer(shooterUuid);
        var target = playerManager.getPlayer(targetUuid);

        if (shooter == null || target == null) {
            LasertagMod.LOGGER.error("playerHitPlayer where shooter or target is offline. Shooter uuid: " + shooterUuid + ", Target uuid: " + targetUuid);
            return;
        }

        playerHitPlayer(shooter, target);
    }

    /**
     * This gets called when a player hit a lasertarget
     *
     * @param shooter The player who fired
     * @param target The lasertarget who got hit
     */
    @Override
    public void playerHitLasertarget(ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        target.setHit();

        // Check that target is activated
        if (target.isDeactivated()) {
            return;
        }

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        // Check that player didn't hit the target before
        if (!gameMode.canLasertargetsBeHitMutlipleTimes() && target.alreadyHitBy(shooter)) {
            return;
        }

        // Register on server
        lasertargetManager.registerLasertarget(target);

        // Get the world
        var world = server.getOverworld();

        // Get the old block state
        var oldBlockState = world.getBlockState(target.getPos());

        // Delegate to game mode
        gameMode.onPlayerHitLasertarget(this.server, shooter, target);

        // Deactivate
        target.setDeactivated(true);

        // Reactivate after configured amount of seconds
        var deactivationThread = ThreadUtil.createScheduledExecutor("server-lasertag-target-deactivation-thread-%d");
        deactivationThread.schedule(() -> {

            // Get the old block state
            var oldBlockStateReset = world.getBlockState(target.getPos());

            target.setDeactivated(false);

            // Get the new block state
            var newBlockState = world.getBlockState(target.getPos());

            // Send lasertag updated to clients
            world.updateListeners(target.getPos(), oldBlockStateReset, newBlockState, Block.NOTIFY_LISTENERS);

            deactivationThread.shutdownNow();
        }, settingsManager.<Long>get(SettingDescription.LASERTARGET_DEACTIVATE_TIME), TimeUnit.SECONDS);

        // Add player to the players who hit the target
        target.addHitBy(shooter);

        // Get the new block state
        var newBlockState = world.getBlockState(target.getPos());

        // Send lasertag updated to clients
        world.updateListeners(target.getPos(), oldBlockState, newBlockState, Block.NOTIFY_LISTENERS);
    }

    /**
     * This gets called when a player hit a lasertarget
     *
     * @param shooterUuid The uuid of the player who fired
     * @param targetPos The block pos of the lasertarget who got hit
     */
    @Override
    public void playerHitLasertarget(UUID shooterUuid, BlockPos targetPos) {
        var playerManager = server.getPlayerManager();

        var shooter = playerManager.getPlayer(shooterUuid);

        // getBlockEntity must be executed on server thread
        server.execute(() -> {
            var target = server.getOverworld().getBlockEntity(targetPos);

            if (shooter == null || target == null) {
                LasertagMod.LOGGER.error("playerHitLasertarget where shooter is offline or target not found. Shooter uuid: " + shooterUuid + ", Target pos: (" + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ() + ")");
                return;
            }

            playerHitLasertarget(shooter, (LaserTargetBlockEntity)target);
        });
    }

    @Override
    public void dispose() {
        synchronized (this) {
            if (gameTickTimer == null) {
                return;
            }
            gameTickTimer.shutdownNow();
            gameTickTimer = null;
        }
    }

    @Override
    public void syncStateToClient(ServerPlayerEntity client) {

        // Serialize to json
        var jsonString = syncedState.toJson();

        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write json to buffer
        buf.writeString(jsonString);

        // Send to client
        ServerPlayNetworking.send(client, NetworkingConstants.STATE_SYNC, buf);
    }

    /**
     * This method is called every minute when the game is running
     */
    @Override
    public void doTick(boolean isLastNormalTick) {

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        // Delegate to game mode
        gameMode.onTick(this.server);

        if (!(server.getOverworld().getChunkManager().getChunkGenerator() instanceof ArenaChunkGenerator arenaChunkGenerator)) {
            return;
        }
        this.musicManager.tick(arenaChunkGenerator.getConfig().getType(), isLastNormalTick);
    }

    @Override
    public void endTick() {

        lasertagGameOver();
    }

    @Override
    public void thirtySecondsTick() {
        if (!(server.getOverworld().getChunkManager().getChunkGenerator() instanceof ArenaChunkGenerator arenaChunkGenerator)) {
            return;
        }
        this.musicManager.playOutro(arenaChunkGenerator.getConfig().getType());
    }

    @Override
    public ISyncedState getSyncedState() {
        return syncedState;
    }

    //endregion

    //region Sub-manager getters

    @Override
    public IArenaManager getArenaManager() {
        return arenaManager;
    }

    @Override
    public IBlockTickManager getBlockTickManager() {
        return blockTickManager;
    }

    @Override
    public ISettingsPresetsManager getSettingsPresetsManager() {
        return settingsPresetsManager;
    }

    @Override
    public ISpawnpointManager getSpawnpointManager() {
        return spawnpointManager;
    }

    @Override
    public IActivationManager getActivationManager() {
        return activationManager;
    }

    @Override
    public ICaptureTheFlagManager getCaptureTheFlagManager() {
        return captureTheFlagManager;
    }

    @Override
    public IGameModeManager getGameModeManager() {
        return gameModeManager;
    }

    @Override
    public IScoreManager getScoreManager() {
        return scoreManager;
    }

    @Override
    public ISettingsManager getSettingsManager() {
        return settingsManager;
    }

    @Override
    public ISettingsPresetsNameManager getSettingsPresetsNameManager() {
        return settingsPresetsNameManager;
    }

    @Override
    public ITeamsManager getTeamsManager() {
        return teamsManager;
    }

    @Override
    public IMusicalChairsManager getMusicalChairsManager() {
        return musicalChairsManager;
    }

    @Override
    public IEliminationManager getEliminationManager() {
        return eliminationManager;
    }

    //endregion

    //region Private methods

    /**
     * This method is called when the game ends
     */
    private void lasertagGameOver() {

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        // Delegate to game mode
        gameMode.onGameEnd(this.server);

        // Stop all music from playing
        musicManager.stopMusic();

        isRunning = false;
        syncedState.getUIState().isGameRunning = false;

        ServerEventSending.sendToEveryone(server, NetworkingConstants.GAME_OVER, PacketByteBufs.empty());

        // Reset server internal hud render manager
        uiStateManager.stopGameTimer();

        // Reset lasertargets
        lasertargetManager.resetLasertargets();

        // Reset music manager
        musicManager.reset();

        // Generate statistics
        this.generateStats();

        // Clean up (stop game tick timer)
        dispose();
    }

    private void generateStats() {
        try {
            // Calculate stats
            var stats = StatsCalculator.calcStats(this,
                    syncedState.getUIState().gameTime);

            // Create packet
            var buf = new PacketByteBuf(Unpooled.buffer());

            // Get gson builder
            var gsonBuilder = new GsonBuilder();

            // Get serializer
            var serializer = TeamDtoSerializer.getSerializer();

            // Register type adapter
            gsonBuilder.registerTypeAdapter(TeamDto.class, serializer);

            // To json
            var jsonString = gsonBuilder.create().toJson(stats, GameStats.class);

            // Write to buffer
            buf.writeString(jsonString);

            // Send statistics to clients
            ServerEventSending.sendToEveryone(server, NetworkingConstants.GAME_STATISTICS, buf);
        } catch (Exception e) {
            LasertagMod.LOGGER.error("ERROR:", e);
        }
    }

    //endregion
}
