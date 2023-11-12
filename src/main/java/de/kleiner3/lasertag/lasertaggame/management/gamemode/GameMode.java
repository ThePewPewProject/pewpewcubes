package de.kleiner3.lasertag.lasertaggame.management.gamemode;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.types.ScoreHolding;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.LasertagSettingsMap;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.spawnpoints.LasertagSpawnpointManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.ArrayList;
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

    /**
     * Flag to indicate if lasertargets can be hit multiple times by the same player
     */
    private final boolean lasertargetsCanBeHitMultipleTimes;

    public GameMode(String translatableName, boolean infiniteTime, boolean teamsActive, boolean lasertargetsCanBeHitMultipleTimes) {
        this.translatableName = translatableName;
        this.infiniteTime = infiniteTime;
        this.teamsActive = teamsActive;
        this.lasertargetsCanBeHitMultipleTimes = lasertargetsCanBeHitMultipleTimes;
    }

    //region Interface methods

    /**
     * Creates a settings map containing the default setting values for this game mode.
     * The default implementation simply returns the base settings.
     * <br>
     * Override if the game mode has special default settings.
     * <br>
     * This method is called when switching the game mode or resetting the settings.
     *
     * @return The default settings map
     */
    public LasertagSettingsMap createDefaultSettings() {
        return LasertagSettingsMap.createBaseSettings();
    }

    /**
     * Gets the settings which have an effect in this game mode.
     * The default implementation returns the settings that are used by all game modes.
     * <br>
     * This method gets used in the game managers settings screen.
     *
     * @return The settings having an effect in this game mode.
     */
    public List<SettingDescription> getRelevantSettings() {
        var list = new ArrayList<SettingDescription>();

        list.add(SettingDescription.WEAPON_COOLDOWN);
        list.add(SettingDescription.WEAPON_REACH);
        list.add(SettingDescription.SHOW_LASER_RAYS);
        list.add(SettingDescription.MAX_TEAM_SIZE);
        list.add(SettingDescription.RENDER_TEAM_LIST);
        list.add(SettingDescription.RENDER_TIMER);
        list.add(SettingDescription.PREGAME_DURATION);
        list.add(SettingDescription.PLAYER_DEACTIVATE_TIME);
        list.add(SettingDescription.LASERTARGET_DEACTIVATE_TIME);
        list.add(SettingDescription.GEN_STATS_FILE);
        list.add(SettingDescription.AUTO_OPEN_STATS_FILE);
        list.add(SettingDescription.DO_ORIGIN_SPAWN);
        list.add(SettingDescription.RESPAWN_PENALTY);
        list.add(SettingDescription.SHOW_NAMETAGS_OF_TEAMMATES);
        list.add(SettingDescription.MINING_FATIGUE_ENABLED);

        return list;
    }

    /**
     * Checks if all starting conditions are met. If the game can start, this method returns an empty optional
     * Otherwise it returns the reasons why the game can not start as a string.
     * <br>
     * Override if the game mode has other or additional starting conditions.
     * <br>
     * This method is called as the first step when starting a lasertag game.
     *
     * @param server            The server this game runs on
     * @param spawnpointManager The lasertag spawnpoint manager of the server
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
     * @param server            The server this game runs on
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
     * Sets the state to be ready for a new lasertag game. Resets scores and states.
     * The default implementation sets the game rules keepInventory to true and doImmediateRespawn to true.
     * <br>
     * Override this method if the game mode needs special preparation.
     * <br>
     * This method is called just after the players got teleported to their spawnpoints
     * and just as the pre game timer starts.
     *
     * @param server The server this game runs on
     */
    public void onPreGameStart(MinecraftServer server) {

        // Set gamerules
        var gameRules = server.getGameRules();
        gameRules.get(GameRules.KEEP_INVENTORY).set(true, server);

        var doImmediateRespawn = false;
        long respawnCooldownSetting = LasertagGameManager.getInstance().getSettingsManager().get(SettingDescription.RESPAWN_PENALTY);
        if (respawnCooldownSetting == 0L) {
            doImmediateRespawn = true;
        }
        gameRules.get(GameRules.DO_IMMEDIATE_RESPAWN).set(doImmediateRespawn, server);
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

        // Activate every player
        LasertagGameManager.getInstance().getDeactivatedManager().activateAll(server);
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
     * The game mode's logic for if a player hits a lasertarget. The default implementation triggers the score sound
     * event for the shooting player.
     *
     * @param server  The server this game runs on
     * @param shooter The player who fired the laser ray
     * @param target  The lasertarget that got hit
     */
    public void onPlayerHitLasertarget(MinecraftServer server, ServerPlayerEntity shooter, LaserTargetBlockEntity target) {
        ServerEventSending.sendPlayerSoundEvent(shooter, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND);
    }

    /**
     * The game mode's logic for if a player hits another player. the default implementation triggers the score sound
     * event for the shooting player.
     *
     * @param server  The server this game runs on
     * @param shooter The player who fired the laser ray
     * @param target  The player who got hit
     */
    public void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target) {
        ServerEventSending.sendPlayerSoundEvent(shooter, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND);
    }

    /**
     * The game mode's logic for if a player dies. Implement this if the game mode requires custom player death logic.
     *
     * @param server The server this game runs on
     * @param player The player who died
     * @param source The damage source which resulted in the player dying
     */
    public abstract void onPlayerDeath(MinecraftServer server, ServerPlayerEntity player, DamageSource source);

    /**
     * Checks the game over conditions and ends the game if they are met. Only used for game modes that are not
     * time-limited. Must be called every time a game over condition changes.
     * <br>
     * The default implementation just prints an error message as this must be implemented by the concrete game mode
     * or should not be used.
     *
     * @param server The server this game runs on
     */
    public void checkGameOver(MinecraftServer server) {
        LasertagMod.LOGGER.error("checkGameOver not implemented.", new Exception("Not implemented"));
    }

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
        LasertagGameManager.getInstance().getDeactivatedManager().deactivateAll(server);

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

    /**
     * Gets the team id of the winning team or -1 if something went wrong.
     *
     * @return The id of the winning team
     */
    public abstract int getWinnerTeamId();

    /**
     * Get the team score text for the team list. This will be rendered beside the team name if teams are active
     * in the currently selected game mode.
     * <br>
     * This can for example be a simple text containing the score or a text containing the number of flags a team has
     * with an icon.
     *
     * @param team The team to get the score text of
     * @return The text containing the team score
     */
    public abstract Text getTeamScoreText(TeamDto team);

    /**
     * Get the player score text for the team list. This will be rendered beside the player name.
     * <br>
     * This can for example be a simple text containing the score of the player of an icon of a flag if the player
     * is holding a flag.
     *
     * @param playerUuid The UUID of the player to get the score text of
     * @return The text containing the player score
     */
    public abstract Text getPlayerScoreText(UUID playerUuid);

    /**
     * Get the final score of a team. Used for statistics calculation.
     *
     * @param team The team to get the score of
     * @return A ScoreHolding object holding the score of the team
     */
    public abstract ScoreHolding getTeamFinalScore(TeamDto team);

    /**
     * Get the final score of a player. Used for statistics calculation.
     *
     * @param playerUuid The uuid of the player to get the score of
     * @return A ScoreHolding object holding the score of the player
     */
    public abstract ScoreHolding getPlayerFinalScore(UUID playerUuid);

    //endregion

    //region Public methods

    public String getTranslatableName() {
        return this.translatableName;
    }

    public boolean hasInfiniteTime() {
        return this.infiniteTime;
    }

    public boolean areTeamsActive() {
        return this.teamsActive;
    }

    public boolean canLasertargetsBeHitMutlipleTimes() {
        return this.lasertargetsCanBeHitMultipleTimes;
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
