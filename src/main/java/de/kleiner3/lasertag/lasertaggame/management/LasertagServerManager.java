package de.kleiner3.lasertag.lasertaggame.management;

import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.ITickable;
import de.kleiner3.lasertag.lasertaggame.management.lasertargets.LasertargetManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.spawnpoints.LasertagSpawnpointManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamDtoSerializer;
import de.kleiner3.lasertag.lasertaggame.statistics.GameStats;
import de.kleiner3.lasertag.lasertaggame.statistics.StatsCalculator;
import de.kleiner3.lasertag.lasertaggame.timing.GameTickTimerTask;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;
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

    //endregion

    //region Private fields

    private boolean isRunning;
    private ScheduledExecutorService gameTickTimer = null;

    //endregion

    public LasertagServerManager(MinecraftServer server) {

        this.server = server;

        lasertargetManager = new LasertargetManager();
        spawnpointManager = new LasertagSpawnpointManager();

        isRunning = false;
    }

    //region Public methods

    /**
     * Start the lasertag game
     *
     * @return The reasons why the start game got aborted.
     */
    public Optional<String> startGame(boolean scanSpawnpoints) {

        var world = server.getOverworld();

        // Reset all scores
        LasertagGameManager.getInstance().getScoreManager().resetScores(world);

        spawnpointManager.initSpawnpointCacheIfNecessary(world, scanSpawnpoints);

        // Check starting conditions
        var abortReasons = checkStartingConditions();

        // If should abort
        if (abortReasons.isPresent()) {
            // Send abort event to clients
            ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_START_ABORTED, new PacketByteBuf(Unpooled.buffer()));
            return abortReasons;
        }

        // Set gamerules
        var gameRules = server.getGameRules();
        gameRules.get(GameRules.KEEP_INVENTORY).set(true, server);
        gameRules.get(GameRules.DO_IMMEDIATE_RESPAWN).set(true, server);

        // Get the team manager
        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        // Get the team config manager
        var teamConfigManager = teamManager.getTeamConfigManager();

        // Teleport players
        for (var teamDto : teamConfigManager.teamConfig.values()) {
            var team = teamManager.getPlayersOfTeam(teamDto);

            // Handle spectators
            if (teamDto.equals(TeamConfigManager.SPECTATORS)) {

                sendSpectatorsToSpawnpoints(team);

                continue;
            }

            sendTeamToSpawnpoints(teamDto, team);
        }

        // Start game
        isRunning = true;

        var preGameDelayTimer = ThreadUtil.createScheduledExecutor("lasertag-server-pregame-delay-timer-thread-%d");
        var preGameDelay = LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PREGAME_DURATION);

        preGameDelayTimer.schedule(() -> {

            // Activate every player
            teamManager.forEachPlayer((teamDto, playerUuid) -> {

                LasertagGameManager.getInstance().getDeactivatedManager().activate(playerUuid, world, server.getPlayerManager());
                var player = server.getPlayerManager().getPlayer(playerUuid);

                // Sanity check
                if (player == null) {
                    return;
                }

                player.onActivated();
            });

            // Start game tick timer
            gameTickTimer = ThreadUtil.createScheduledExecutor("lasertag-server-game-tick-timer-thread-%d");
            gameTickTimer.scheduleAtFixedRate(new GameTickTimerTask(this), 0, 1, TimeUnit.MINUTES);

            // Stop the pre game delay timer
            preGameDelayTimer.shutdownNow();

        }, preGameDelay, TimeUnit.SECONDS);

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

        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        var shooterTeam = teamManager.getTeamOfPlayer(shooter.getUuid());
        var targetTeam = teamManager.getTeamOfPlayer(target.getUuid());

        // Check that hit player is not in same team as firing player
        if (shooterTeam.equals(targetTeam)) {
            return;
        }

        // Check if player is deactivated
        if (LasertagGameManager.getInstance().getDeactivatedManager().isDeactivated(target.getUuid())) {
            return;
        }

        var world = server.getOverworld();

        // Deactivate player
        LasertagGameManager.getInstance().getDeactivatedManager().deactivate(target.getUuid(), world, server.getPlayerManager());

        LasertagGameManager.getInstance().getScoreManager().onPlayerScored(world, shooter, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAYER_HIT_SCORE));
        ServerEventSending.sendPlayerSoundEvent(shooter, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND);
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

        LasertagGameManager.getInstance().getScoreManager().onPlayerScored(server.getOverworld(), shooter, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.LASERTARGET_HIT_SCORE));
        ServerEventSending.sendPlayerSoundEvent(shooter, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND);

        // Register on server
        lasertargetManager.registerLasertarget(target);

        // Deactivate
        target.setDeactivated(true);

        // Reactivate after configured amount of seconds
        var deactivationThread = ThreadUtil.createScheduledExecutor("lasertag-target-deactivation-thread-%d");
        deactivationThread.schedule(() -> {
            target.setDeactivated(false);

            deactivationThread.shutdownNow();
        }, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.LASERTARGET_DEACTIVATE_TIME), TimeUnit.SECONDS);

        // Add player to the players who hit the target
        target.addHitBy(shooter);
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
    public void doTick() {
        // Here the music can be started
    }

    @Override
    public void endTick() {

        lasertagGameOver();
    }

    //endregion

    //region Private methods

    private void sendSpectatorsToSpawnpoints(List<UUID> spectators) {

        for (var playerUuid : spectators) {

            // Get the player
            var player = server.getPlayerManager().getPlayer(playerUuid);

            // Sanity check
            if (player == null) {
                continue;
            }

            // Set player to spectator gamemode
            player.changeGameMode(GameMode.SPECTATOR);

            // Get all spawnpoints
            var spawnpoints = spawnpointManager.getAllSpawnpoints();

            // Get random index
            var idx = server.getOverworld().getRandom().nextInt(spawnpoints.size());

            var destination = spawnpoints.get(idx);
            player.requestTeleport(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);
        }
    }

    private void sendTeamToSpawnpoints(TeamDto teamDto, List<UUID> playerUuids) {

        for (var playerUuid : playerUuids) {

            // Get spawnpoints
            var spawnpoints = spawnpointManager.getSpawnpoints(teamDto);

            var player = server.getPlayerManager().getPlayer(playerUuid);

            // Sanity check
            if (player == null) {
                continue;
            }

            int idx = server.getOverworld().getRandom().nextInt(spawnpoints.size());

            var destination = spawnpoints.get(idx);
            player.requestTeleport(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);

            // Set player to survival by default -> player can break blocks
            var newGamemode = GameMode.SURVIVAL;

            // If setting miningFatigueEnabled is true
            if (LasertagGameManager.getInstance().getSettingsManager().get(SettingDescription.MINING_FATIGUE_ENABLED)) {
                // Set player to adventure game mode -> player can't break blocks
                newGamemode = GameMode.ADVENTURE;
            }

            player.changeGameMode(newGamemode);

            // Get spawn pos
            var spawnPos = new BlockPos(destination.getX(), destination.getY() + 1, destination.getZ());

            // Set players spawnpoint
            player.setSpawnPoint(
                    World.OVERWORLD,
                    spawnPos, 0.0F, true, false);
        }
    }

    /**
     * Checks if all starting conditions are met. If the game can start, this method returns an empty optional
     * Otherwise it returns the reasons why the game can not start as a string.
     *
     * @return Optional containing the abort reasons if there were any. Otherwise Optional.empty()
     */
    private Optional<String> checkStartingConditions() {
        boolean abort = false;
        var builder = new StringBuilder();

        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        // For every team
        for (var team : teamManager.getTeamMap().entrySet()) {

            // Skip spectators
            if (team.getKey().equals(TeamConfigManager.SPECTATORS)) {

                continue;
            }

            // If the team contains players
            if (team.getValue().size() > 0) {
                // Get the spawnpoints for the team
                var spawnpoints = spawnpointManager.getSpawnpoints(team.getKey());

                // If the team has no spawnpoints
                if (spawnpoints.size() == 0) {
                    abort = true;
                    builder.append("  *No spawnpoints were found for team '" + team.getKey().name() + "'\n");
                }
            }
        }

        // Get the players without team
        var playersWithoutTeam = server.getPlayerManager().getPlayerList().stream()
                .filter(playerListEntry -> !teamManager.isPlayerInTeam(playerListEntry.getUuid()))
                .toList();

        if (playersWithoutTeam.size() > 0) {

            abort = true;
            for (var player : playersWithoutTeam) {

                builder.append("  *Player '" + player.getLasertagUsername() + "' has no team\n");
            }
        }

        if (abort) {
            return Optional.of(builder.toString());
        }

        return Optional.empty();
    }

    /**
     * This method is called when the game ends
     */
    private void lasertagGameOver() {

        isRunning = false;

        var world = server.getOverworld();

        ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_OVER, PacketByteBufs.empty());

        // Reset server internal hud render manager
        LasertagGameManager.getInstance().getHudRenderManager().stopGameTimer();
        LasertagGameManager.getInstance().getHudRenderManager().shouldRenderNameTags = true;

        // Deactivate every player
        LasertagGameManager.getInstance().getDeactivatedManager().deactivateAll(world, server.getPlayerManager());

        // Teleport players back to spawn
        LasertagGameManager.getInstance().getTeamManager().forEachPlayer(((team, playerUuid) -> {
            var player = server.getPlayerManager().getPlayer(playerUuid);

            // Sanity check
            if (player == null) {
                return;
            }

            player.requestTeleport(0.5F, 1, 0.5F);

            // Create block pos
            var origin = new BlockPos(0, 1, 0);

            // Set players spawnpoint
            player.setSpawnPoint(
                    World.OVERWORLD,
                    origin, 0.0F, true, false);

            player.changeGameMode(GameMode.ADVENTURE);
        }));

        // Reset lasertargets
        lasertargetManager.resetLasertargets();

        try {
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

        // Clean up (stop game tick timer)
        dispose();
    }

    //endregion
}
