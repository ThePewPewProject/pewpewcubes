package de.kleiner3.lasertag.lasertaggame.management;

import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.ITickable;
import de.kleiner3.lasertag.lasertaggame.management.lasertargets.LasertargetManager;
import de.kleiner3.lasertag.lasertaggame.management.map.LasertagMapManager;
import de.kleiner3.lasertag.lasertaggame.management.music.LasertagMusicManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.settings.presets.LasertagSettingsPresetsManager;
import de.kleiner3.lasertag.lasertaggame.management.spawnpoints.LasertagSpawnpointManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamDtoSerializer;
import de.kleiner3.lasertag.lasertaggame.statistics.GameStats;
import de.kleiner3.lasertag.lasertaggame.statistics.StatsCalculator;
import de.kleiner3.lasertag.lasertaggame.timing.GameTickTimerTask;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to manage the lasertag game server-side. THIS DOES NOT GET SYNCED TO THE CLIENTS!
 *
 * @author Étienne Muser
 */
public class LasertagServerManager implements IManager, ITickable {

    //region Necessary injected fields

    private final MinecraftServer server;

    //endregion

    //region Sub-Managers

    private LasertargetManager lasertargetManager;

    private LasertagSpawnpointManager spawnpointManager;

    private LasertagSettingsPresetsManager settingsPresetsManager;

    private LasertagMusicManager musicManager;

    private LasertagMapManager mapManager;

    //endregion

    //region Private fields

    private boolean isRunning;
    private ScheduledExecutorService gameTickTimer = null;

    //endregion

    public LasertagServerManager(MinecraftServer server) {

        this.server = server;

        lasertargetManager = new LasertargetManager();
        spawnpointManager = new LasertagSpawnpointManager();
        settingsPresetsManager = new LasertagSettingsPresetsManager();
        musicManager = new LasertagMusicManager(server);
        mapManager = new LasertagMapManager(server);

        isRunning = false;
    }

    //region Public methods

    /**
     * Start the lasertag game
     *
     * @return The reasons why the start game got aborted.
     */
    public Optional<String> startGame(boolean scanSpawnpoints) {

        // Get the game mode
        var gameMode = LasertagGameManager.getInstance().getGameModeManager().getGameMode();

        var world = server.getOverworld();

        spawnpointManager.initSpawnpointCacheIfNecessary(world, scanSpawnpoints);

        // Check starting conditions
        var abortReasons = gameMode.checkStartingConditions(this.server, this.spawnpointManager);

        // If should abort
        if (abortReasons.isPresent()) {
            // Send abort event to clients
            ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_START_ABORTED, new PacketByteBuf(Unpooled.buffer()));
            return abortReasons;
        }

        // Delegate to the game mode
        gameMode.sendPlayersToSpawnpoints(this.server, this.spawnpointManager);

        // Start game
        isRunning = true;

        var preGameDelayTimer = ThreadUtil.createScheduledExecutor("lasertag-server-pregame-delay-timer-thread-%d");
        var preGameDelay = LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PREGAME_DURATION);

        if (server.getOverworld().getChunkManager().getChunkGenerator() instanceof ArenaChunkGenerator arenaChunkGenerator) {
            musicManager.playIntro(arenaChunkGenerator.getConfig().getType());
        }

        preGameDelayTimer.schedule(() -> {

            // Delegate to game mode
            gameMode.onGameStart(this.server);

            // Start game tick timer
            gameTickTimer = ThreadUtil.createScheduledExecutor("lasertag-server-game-tick-timer-thread-%d");
            gameTickTimer.scheduleAtFixedRate(new GameTickTimerTask(this), 0, 1, TimeUnit.SECONDS);

            // Stop the pre game delay timer
            preGameDelayTimer.shutdownNow();

        }, preGameDelay, TimeUnit.SECONDS);

        // Delegate to the game mode
        gameMode.onPreGameStart(this.server);

        // Notify players
        ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_STARTED, PacketByteBufs.empty());

        // If is on dedicated server
        if (server.isDedicated()) {

            // Set render data on server
            var renderData = LasertagGameManager.getInstance().getHudRenderManager();

            renderData.progress = 0.0;
            renderData.shouldRenderNameTags = false;

            // Start pregame count down timer
            renderData.startPreGameCountdownTimer(LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PREGAME_DURATION));
        }

        return Optional.empty();
    }

    /**
     * Stops the running lasertag game
     * @return False if there was no game running. Otherwise, true.
     */
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
    public boolean isGameRunning() {
        return isRunning;
    }

    /**
     * This gets called when a player hit another player
     *
     * @param shooter The player who fired
     * @param target The player who got hit
     */
    public void playerHitPlayer(ServerPlayerEntity shooter, ServerPlayerEntity target) {

        // Get the game mode
        var gameMode = LasertagGameManager.getInstance().getGameModeManager().getGameMode();

        gameMode.onPlayerHitPlayer(this.server, shooter, target);
    }

    /**
     * This gets called when a player hit another player
     *
     * @param shooterUuid The uuid of the player who fired
     * @param targetUuid The uuid of the player who got hit
     */
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
    public void playerHitLasertarget(ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        // Check that target is activated
        if (target.isDeactivated()) {
            return;
        }

        // Check that player didn't hit the target before
        if (target.alreadyHitBy(shooter)) {
            return;
        }

        // Get the game mode
        var gameMode = LasertagGameManager.getInstance().getGameModeManager().getGameMode();

        // Register on server
        lasertargetManager.registerLasertarget(target);

        // Delegate to game mode
        gameMode.onPlayerHitLasertarget(this.server, shooter, target);
    }

    /**
     * This gets called when a player hit a lasertarget
     *
     * @param shooterUuid The uuid of the player who fired
     * @param targetPos The block pos of the lasertarget who got hit
     */
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

    /**
     * This method is called every minute when the game is running
     */
    @Override
    public void doTick(boolean isLastNormalTick) {

        // Get the game mode
        var gameMode = LasertagGameManager.getInstance().getGameModeManager().getGameMode();

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

    public LasertagSettingsPresetsManager getSettingsPresetsManager() {
        return this.settingsPresetsManager;
    }

    public LasertagMapManager getMapManager() { return this.mapManager; }

    public LasertagSpawnpointManager getSpawnpointManager() { return this.spawnpointManager; }

    //endregion

    //region Private methods

    /**
     * This method is called when the game ends
     */
    private void lasertagGameOver() {

        // Get the game mode
        var gameMode = LasertagGameManager.getInstance().getGameModeManager().getGameMode();

        // Delegate to game mode
        gameMode.onGameEnd(this.server);

        isRunning = false;

        ServerEventSending.sendToEveryone(server.getOverworld(), NetworkingConstants.GAME_OVER, PacketByteBufs.empty());

        // Reset server internal hud render manager
        LasertagGameManager.getInstance().getHudRenderManager().stopGameTimer();
        LasertagGameManager.getInstance().getHudRenderManager().shouldRenderNameTags = true;

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
            var world = server.getOverworld();

            // Calculate stats
            var stats = StatsCalculator.calcStats(LasertagGameManager.getInstance().getPlayerManager());

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
            ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_STATISTICS, buf);
        } catch (Exception e) {
            LasertagMod.LOGGER.error("ERROR:", e);
        }
    }

    //endregion
}
