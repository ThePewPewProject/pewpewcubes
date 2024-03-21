package de.pewpewproject.lasertag.lasertaggame.state.management.server.implementation;

import com.google.gson.GsonBuilder;
import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.block.entity.LaserTargetBlockEntity;
import de.pewpewproject.lasertag.common.util.ThreadUtil;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.*;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.*;
import de.pewpewproject.lasertag.lasertaggame.state.synced.ISyncedState;
import de.pewpewproject.lasertag.lasertaggame.statistics.GameStats;
import de.pewpewproject.lasertag.lasertaggame.statistics.StatsCalculator;
import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;
import de.pewpewproject.lasertag.lasertaggame.team.serialize.TeamDtoSerializer;
import de.pewpewproject.lasertag.lasertaggame.timing.GameTickTimerTask;
import de.pewpewproject.lasertag.networking.NetworkingConstants;
import de.pewpewproject.lasertag.networking.server.ServerEventSending;
import de.pewpewproject.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
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
import java.util.concurrent.*;
import java.util.function.Function;

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

    /**
     * Flag to indicate that the pre game count down of
     * this game has already passed
     */
    private boolean hasPreGamePassed;

    private ScheduledExecutorService gameTickTimer = null;

    private ScheduledExecutorService preGameDelayTimer = null;

    /**
     * The synchronized state of the game.
     * This state must be synchronized with the clients.
     */
    private final ISyncedState syncedState;

    //endregion

    //region Server sub-managers

    private final IArenaManager arenaManager;
    private final IBlockTickManager blockTickManager;
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
    private final ILasertargetsManager lasertargetsManager;

    //endregion

    public ServerLasertagManager(MinecraftServer server,
                                 ISyncedState syncedState,
                                 IArenaManager arenaManager,
                                 IBlockTickManager blockTickManager,
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
                                 IEliminationManager eliminationManager,
                                 ILasertargetsManager lasertargetsManager) {

        this.server = server;
        this.syncedState = syncedState;
        this.eliminationManager = eliminationManager;
        this.lasertargetsManager = lasertargetsManager;
        isRunning = false;
        hasPreGamePassed = false;

        this.arenaManager = arenaManager;
        this.blockTickManager = blockTickManager;
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
    public CompletableFuture<Optional<String>> startGame(boolean scanSpawnpoints) {

        // If the reload arena setting is set
        if (settingsManager.<Boolean>get(SettingDescription.RELOAD_ARENAS_BEFORE_GAME)) {

            LasertagMod.LOGGER.info("[ServerLasertagManager] reloading arena...");

            // Reload the arena
            arenaManager.reloadArena();
        }

        Function<Void, Optional<String>> arenaLoadCallback = ignored -> {

            // If there is already a game running
            if (isRunning) {
                return Optional.of("There is already a game running");
            }

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

            preGameDelayTimer = ThreadUtil.createScheduledExecutor("server-lasertag-server-pregame-delay-timer-thread-%d");
            var preGameDelay = settingsManager.<Long>get(SettingDescription.PREGAME_DURATION);

            if (world.getChunkManager().getChunkGenerator() instanceof ArenaChunkGenerator arenaChunkGenerator) {
                musicManager.playIntro(arenaChunkGenerator.getConfig().getType());
            }

            preGameDelayTimer.schedule(() -> {

                hasPreGamePassed = true;

                // Delegate to game mode
                gameMode.onGameStart(this.server);

                // Start game tick timer
                gameTickTimer = ThreadUtil.createScheduledExecutor("server-lasertag-game-tick-timer-thread-%d");
                gameTickTimer.scheduleAtFixedRate(new GameTickTimerTask(this, gameModeManager, settingsManager), 0, 1, TimeUnit.SECONDS);

                // Stop the pre game delay timer
                shutdownPreGameDelayTimer();

            }, preGameDelay, TimeUnit.SECONDS);

            // Delegate to the game mode
            gameMode.onPreGameStart(this.server);

            // Notify players
            ServerEventSending.sendToEveryone(server, NetworkingConstants.GAME_STARTED, PacketByteBufs.empty());

            // Start pregame count down timer
            uiStateManager.startPreGameCountdownTimer(settingsManager.<Long>get(SettingDescription.PREGAME_DURATION));

            return Optional.empty();
        };

        return arenaManager.getLoadArenaFuture().thenApplyAsync(arenaLoadCallback, server);
    }

    /**
     * Stops the running lasertag game
     *
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

    @Override
    public boolean hasPreGamePassed() {
        return hasPreGamePassed;
    }

    /**
     * This gets called when a player hit another player
     *
     * @param shooter The player who fired
     * @param target  The player who got hit
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
     * @param targetUuid  The uuid of the player who got hit
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
     * @param target  The lasertarget who got hit
     */
    @Override
    public void playerHitLasertarget(ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        lasertargetsManager.setLastHitTime(target.getPos(), server.getOverworld().getTime());

        // Check that target is activated
        if (lasertargetsManager.isDeactivated(target.getPos())) {
            return;
        }

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        // Check that player didn't hit the target before
        if (!gameMode.canLasertargetsBeHitMutlipleTimes() && lasertargetsManager.isAlreadyHitBy(target.getPos(), shooter.getUuid())) {
            return;
        }

        // Get the world
        var world = server.getOverworld();

        // Get the old block state
        var oldBlockState = world.getBlockState(target.getPos());

        // Delegate to game mode
        gameMode.onPlayerHitLasertarget(this.server, shooter, target);

        // Deactivate
        lasertargetsManager.deactivate(target.getPos());

        // Add player to the players who hit the target
        lasertargetsManager.setHitBy(target.getPos(), shooter.getUuid());

        // Get the new block state
        var newBlockState = world.getBlockState(target.getPos());

        // Send lasertag updated to clients
        world.updateListeners(target.getPos(), oldBlockState, newBlockState, Block.NOTIFY_LISTENERS);
    }

    /**
     * This gets called when a player hit a lasertarget
     *
     * @param shooterUuid The uuid of the player who fired
     * @param targetPos   The block pos of the lasertarget who got hit
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

            playerHitLasertarget(shooter, (LaserTargetBlockEntity) target);
        });
    }

    @Override
    public synchronized void dispose() {

        shutdownPreGameDelayTimer();

        if (gameTickTimer == null) {
            return;
        }
        gameTickTimer.shutdownNow();
        gameTickTimer = null;
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

    @Override
    public ILasertargetsManager getLasertargetsManager() {
        return lasertargetsManager;
    }

    //endregion

    //region Private methods

    private synchronized void shutdownPreGameDelayTimer() {

        if (preGameDelayTimer == null) {
            return;
        }

        preGameDelayTimer.shutdownNow();
        preGameDelayTimer = null;
    }

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
        hasPreGamePassed = false;
        syncedState.getUIState().isGameRunning = false;

        ServerEventSending.sendToEveryone(server, NetworkingConstants.GAME_OVER, PacketByteBufs.empty());

        // Get the game time before resetting it to 0
        var gameTime = syncedState.getUIState().gameTime;

        // Reset server internal hud render manager
        uiStateManager.stopGameTimer();

        // Reset lasertargets
        lasertargetsManager.reset();

        // Reset music manager
        musicManager.reset();

        // Generate statistics
        this.generateStats(gameTime);

        // Clean up (stop game tick timer)
        dispose();
    }

    private void generateStats(long gameTime) {
        try {
            // Calculate stats
            var stats = StatsCalculator.calcStats(this, gameTime);

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
