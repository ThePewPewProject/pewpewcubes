package de.kleiner3.lasertag.lasertaggame.management.gamemode;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.spawnpoints.LasertagSpawnpointManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base class for all game modes. Defines the interface for the game modes.
 *
 * @author Étienne Muser
 */
public abstract class GameMode {

    /**
     * The translatable name of the game mode. Also acts as an unique identifier for the game mode.
     */
    private final String translatableName;
    /**
     * Flag whether this game mode has infinite time (Game ends based on other game events)
     * or not (Game ends after X minutes)
     */
    private final boolean infiniteTime;
    /**
     * Flag whether this game mode uses teams or is an 'everyone vs. everyone' game mode
     */
    private final boolean teamsActive;

    public GameMode(String translatableName, boolean infiniteTime, boolean teamsActive) {
        this.translatableName = translatableName;
        this.infiniteTime = infiniteTime;
        this.teamsActive = teamsActive;
    }

    //region Interface methods

    /**
     * Checks if all starting conditions are met. If the game can start, this method returns an empty optional
     * Otherwise it returns the reasons why the game can not start as a string.
     * <br>
     * Override if the game modes has other or additional starting conditions.
     * <br>
     * This method is called as the first step when starting a lasertag game.
     *
     * @param server The server this game runs on
     * @param spawnpointManager The lasertag spawnpoint manager of the server
     *
     * @return Optional containing the abort reasons if there were any. Otherwise Optional.empty()
     */
    public Optional<String> checkStartingConditions(MinecraftServer server, LasertagSpawnpointManager spawnpointManager) {
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
            if (!team.getValue().isEmpty()) {
                // Get the spawnpoints for the team
                var spawnpoints = spawnpointManager.getSpawnpoints(team.getKey());

                // If the team has no spawnpoints
                if (spawnpoints.isEmpty()) {
                    abort = true;
                    builder.append("  *No spawnpoints were found for team '");
                    builder.append(team.getKey().name());
                    builder.append("'\n");
                }
            }
        }

        // Get the players without team
        var playersWithoutTeam = server.getPlayerManager().getPlayerList().stream()
                .filter(playerListEntry -> !teamManager.isPlayerInTeam(playerListEntry.getUuid()))
                .toList();

        if (!playersWithoutTeam.isEmpty()) {

            abort = true;
            for (var player : playersWithoutTeam) {

                builder.append("  *Player '");
                builder.append(player.getLasertagUsername());
                builder.append("' has no team\n");
            }
        }

        if (abort) {
            return Optional.of(builder.toString());
        }

        return Optional.empty();
    }

    /**
     * Sends every player to a spawnpoint that is assigned to him.
     * <br>
     * Override this method if the game mode has special spawnpoint mechanics.
     * <br>
     * This method is being called if the starting conditions are met and just before
     * executing the <code>onPreGameStart</code> method.
     *
     * @param server The server this game runs on
     * @param spawnpointManager The lasertag spawnpoint manager of the server
     */
    public void sendPlayersToSpawnpoints(MinecraftServer server, LasertagSpawnpointManager spawnpointManager) {

        // Get the team manager
        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        // Get the team config manager
        var teamConfigManager = teamManager.getTeamConfigManager();

        // Teleport players
        for (var teamDto : teamConfigManager.teamConfig.values()) {
            var team = teamManager.getPlayersOfTeam(teamDto);

            // Handle spectators
            if (teamDto.equals(TeamConfigManager.SPECTATORS)) {

                this.sendSpectatorsToSpawnpoints(server, team, spawnpointManager.getAllSpawnpoints());

                continue;
            }

            sendTeamToSpawnpoints(server, teamDto, team, spawnpointManager);
        }
    }

    /**
     * Sets the state to be ready for a new lasertag game. The default implementation
     * resets the scores and sets the game rules.
     * <br>
     * Override this method if the game mode needs special preparation.
     * <br>
     * This method is called just after the players got teleported to their spawnpoints
     * and just as the pre game timer starts.
     *
     * @param server The server this game runs on
     */
    public void onPreGameStart(MinecraftServer server) {

        // Get the overworld
        var world = server.getOverworld();

        // Reset all scores
        LasertagGameManager.getInstance().getScoreManager().resetScores(world);

        // Set gamerules
        var gameRules = server.getGameRules();
        gameRules.get(GameRules.KEEP_INVENTORY).set(true, server);
        gameRules.get(GameRules.DO_IMMEDIATE_RESPAWN).set(true, server);
    }

    /**
     * Sets the players to their active state. The default implementation activates every player.
     * <br>
     * Override this method if the game mode requires special activation methods.
     * <br>
     * This method is called when the pre game countdown ends.
     *
     * @param server The server this game runs on
     */
    public void onGameStart(MinecraftServer server) {

        // Get the overworld
        var world = server.getOverworld();

        // Get the team manager
        var teamManager = LasertagGameManager.getInstance().getTeamManager();

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
    }

    /**
     * Random events can be implemented in this method
     * <br>
     * This method is being called every minute if the game is running.
     *
     * @param server The server this game runs on
     */
    public abstract void onTick(MinecraftServer server);

    /**
     * The game mode's logic for if a player hits a lasertarget
     *
     * @param server The server this game runs on
     * @param shooter The player who fired the laser ray
     * @param target The lasertarget that got hit
     */
    public abstract void onPlayerHitLasertarget(MinecraftServer server, ServerPlayerEntity shooter, LaserTargetBlockEntity target);

    /**
     * The game mode's logic for if a player hits another player
     *
     * @param server The server this game runs on
     * @param shooter The player who fired the laser ray
     * @param target The player who got hit
     */
    public abstract void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target);

    /**
     * Cleans up the game. The default implementation deactivates every player and teleports them back into the lobby.
     * <br>
     * Override this method if the game mode needs some special game cleanup.
     * <br>
     * This method is called right when the game ends.
     *
     * @param server The server this game runs on
     */
    public void onGameEnd(MinecraftServer server) {

        // Deactivate every player
        LasertagGameManager.getInstance().getDeactivatedManager().deactivateAll(server.getOverworld(), server.getPlayerManager());

        // Teleport players to origin
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
                    net.minecraft.world.World.OVERWORLD,
                    origin, 0.0F, true, false);

            player.changeGameMode(net.minecraft.world.GameMode.ADVENTURE);
        }));
    }

    //endregion

    //region Public methods

    public String getTranslatableName() {
        return this.translatableName;
    }

    //endregion

    //region Private methods

    private void sendSpectatorsToSpawnpoints(MinecraftServer server, List<UUID> spectators, List<BlockPos> spawnpoints) {

        for (var playerUuid : spectators) {

            // Get the player
            var player = server.getPlayerManager().getPlayer(playerUuid);

            // Sanity check
            if (player == null) {
                continue;
            }

            // Set player to spectator gamemode
            player.changeGameMode(net.minecraft.world.GameMode.SPECTATOR);

            // Get random index
            var idx = server.getOverworld().getRandom().nextInt(spawnpoints.size());

            var destination = spawnpoints.get(idx);
            player.requestTeleport(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);
        }
    }

    private void sendTeamToSpawnpoints(MinecraftServer server, TeamDto teamDto, List<UUID> playerUuids, LasertagSpawnpointManager spawnpointManager) {

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
            var newGamemode = net.minecraft.world.GameMode.SURVIVAL;

            // If setting miningFatigueEnabled is true
            if (LasertagGameManager.getInstance().getSettingsManager().get(SettingDescription.MINING_FATIGUE_ENABLED)) {
                // Set player to adventure game mode -> player can't break blocks
                newGamemode = net.minecraft.world.GameMode.ADVENTURE;
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

    //endregion
}
