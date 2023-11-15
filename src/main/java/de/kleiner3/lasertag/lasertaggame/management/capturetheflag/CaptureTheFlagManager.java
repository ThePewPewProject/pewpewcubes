package de.kleiner3.lasertag.lasertaggame.management.capturetheflag;

import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.implementation.CaptureTheFlagGameMode;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.settings.valuetypes.CTFFlagHoldingPlayerVisibility;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.inventory.Inventories;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * The manager for the capture the flag game mode.
 *
 * @author Étienne Muser
 */
public class CaptureTheFlagManager implements IManager {

    /**
     * Maps every team's id to the number of flags they have left
     */
    private final HashMap<Integer, Long> teamFlagMap;

    /**
     * Maps every team's id to the time in seconds they survived
     */
    private final HashMap<Integer, Long> teamSurviveTimeMap;

    /**
     * Maps every player to the number of flags he has captured. Only necessary on the server. Does not get
     * synced to the clients.
     */
    private final HashMap<UUID, Long> playerFlagCapturedMap;

    /**
     * Maps every player to the team id of the team he is currently holding the flag of. If a player is currently
     * not holding a flag, he has no entry in this map.
     */
    private final HashMap<UUID, Integer> playerHoldingFlagMap;

    public CaptureTheFlagManager() {
        teamFlagMap = new HashMap<>();
        teamSurviveTimeMap = new HashMap<>();
        playerFlagCapturedMap = new HashMap<>();
        playerHoldingFlagMap = new HashMap<>();
    }

    /**
     * Resets the entire manager on the server
     */
    public void reset(ServerWorld world) {
        this.reset();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.FLAG_RESET, PacketByteBufs.empty());
    }

    /**
     * Resets the entire manager
     */
    public void reset() {
        this.teamFlagMap.clear();
        this.teamSurviveTimeMap.clear();
        this.playerFlagCapturedMap.clear();
        this.playerHoldingFlagMap.clear();

        // Get the game mangager
        var gameManager = LasertagGameManager.getInstance();

        // For every team that is not empty
        gameManager.getTeamManager().getTeamMap().entrySet().stream()
                        .filter(team -> !team.getValue().isEmpty())
                        .forEach(team -> {
            // Put the teams initial flag count
            this.teamFlagMap.put(team.getKey().id(), gameManager.getSettingsManager().get(SettingDescription.FLAG_COUNT));
        });
    }

    /**
     * Set the player to be holding no flag. Removes the flag from the players inventory.
     * Sets the player to not glow. Notifies all clients.
     *
     * @param world The world this game is running in
     * @param playerUuid The uuid of the player who held the flag
     */
    public void playerDropFlag(ServerWorld world, UUID playerUuid) {

        // If player is not holding a flag
        if (!this.playerHoldingFlagMap.containsKey(playerUuid)) {
            // Do nothing
            return;
        }

        var oldTeamId = this.playerHoldingFlagMap.remove(playerUuid);

        // Get the player
        var player = world.getPlayerByUuid(playerUuid);

        // Sanity check
        if (player != null) {

            // Take flag from players inventory
            Inventories.remove(player.getInventory(), stack -> stack.isOf(Items.LASERTAG_FLAG), 1, false);
        }

        // If flag holding players should glow
        if (LasertagGameManager.getInstance().getSettingsManager().getEnum(SettingDescription.CTF_FLAG_HOLDING_PLAYER_VISIBILITY) == CTFFlagHoldingPlayerVisibility.GLOW) {

            // Sanity check
            if (player != null) {

                // Set player not glowing
                player.setGlowing(false);
            }
        }

        notifyPlayersAboutFlagHoldingUpdate(world, playerUuid, null);

        // Get the game mode
        var gameMode = LasertagGameManager.getInstance().getGameModeManager().getGameMode();

        if (!(gameMode instanceof CaptureTheFlagGameMode ctfGameMode)) {
            return;
        }

        // get the team of the flag the player was holding
        var teamOptional = LasertagGameManager.getInstance().getTeamManager().getTeamConfigManager().getTeamOfId(oldTeamId);

        // Place the flags
        teamOptional.ifPresent(team -> ctfGameMode.placeFlags(world, team));
    }

    /**
     * Sets a player to have picked up a flag and syncs to all clients
     *
     * @param world The world this game is running in
     * @param player The player who picked up the flag
     * @param team The team he picked up the flag of
     */
    public void playerPickedUpFlag(ServerWorld world, ServerPlayerEntity player, TeamDto team) {

        this.sendTeamFlagStolenMessage(world, team);

        this.playerHoldingFlagMap.put(player.getUuid(), team.id());

        // If flag holding players should glow
        if (LasertagGameManager.getInstance().getSettingsManager().getEnum(SettingDescription.CTF_FLAG_HOLDING_PLAYER_VISIBILITY) == CTFFlagHoldingPlayerVisibility.GLOW) {

            // Set player glowing
            player.setGlowing(true);
        }

        this.notifyPlayersAboutFlagHoldingUpdate(world, player.getUuid(), team.id());
    }

    /**
     * Get the team whose flag the player is currently holding
     *
     * @param playerUuid The uuid of the player
     * @return Optional of the team whose flag he is currently holding
     */
    public Optional<TeamDto> getPlayerHoldingFlagTeam(UUID playerUuid) {

        var teamId = this.playerHoldingFlagMap.get(playerUuid);

        if (teamId == null) {
            return Optional.empty();
        }

        return LasertagGameManager.getInstance().getTeamManager().getTeamConfigManager().getTeamOfId(teamId);
    }

    /**
     * Get the number of flags a player has captured
     *
     * @param playerUuid The uuid of the player
     * @return The number of captured flags
     */
    public long getNumberofCapturedFlags(UUID playerUuid) {
        var numberFromCache = this.playerFlagCapturedMap.get(playerUuid);

        return numberFromCache != null ? numberFromCache : 0;
    }

    /**
     * Get the time in seconds a team survived
     *
     * @param team The team
     * @return Optional containing the time or Optional.empty() if the team survived to the end
     */
    public Optional<Long> getSurviveTime(TeamDto team) {
        return Optional.ofNullable(this.teamSurviveTimeMap.get(team.id()));
    }

    public void updateTeamFlagCount(TeamDto team, long newFlagCount) {
        this.teamFlagMap.put(team.id(), newFlagCount);
    }

    public void updateFlagHolding(UUID playerUuid, Integer teamId) {
        this.playerHoldingFlagMap.put(playerUuid, teamId);
    }

    /**
     * Gets the number of flags a team has left.
     *
     * @param team The team
     * @return The number of flags
     */
    public Long getNumberOfFlags(TeamDto team) {
        var numberOfFlags = this.teamFlagMap.get(team.id());
        return numberOfFlags != null ? numberOfFlags : 0L;
    }

    /**
     * Call this to set a flag of a team to be captured. Also syncs to all clients.
     *
     * @param world The world the game is in
     * @param team The team whose flag got captured
     * @param playerUuid The uuid of the player who captured the flag
     */
    public void flagCaptured(ServerWorld world, TeamDto team, UUID playerUuid) {

        // Get the teams number of flags
        var numberOfFlags = this.teamFlagMap.get(team.id());

        // Remove one flag
        --numberOfFlags;

        // Save the new number of flags
        this.teamFlagMap.put(team.id(), numberOfFlags);

        // Get the old number of flags captured
        var oldFlagCountOptional = Optional.ofNullable(playerFlagCapturedMap.get(playerUuid));

        // Increase
        var newFlagCaptureCount = oldFlagCountOptional.orElse(0L) + 1;

        // Save the new flag capture sound
        this.playerFlagCapturedMap.put(playerUuid, newFlagCaptureCount);

        // Set the player is not holding a flag
        this.playerHoldingFlagMap.remove(playerUuid);

        notifyPlayersAboutFlagCountUpdate(world, team, numberOfFlags);
        notifyPlayersAboutFlagHoldingUpdate(world, playerUuid, null);

        // Get the current game mode
        var gameMode = LasertagGameManager.getInstance().getGameModeManager().getGameMode();

        if (numberOfFlags == 0) {

            this.teamSurviveTimeMap.put(team.id(), LasertagGameManager.getInstance().getHudRenderManager().gameTime);
            this.sendTeamIsOutMessage(world, team);
            this.putTeamToSpectators(world, team);
            gameMode.checkGameOver(world.getServer());
        } else {

            // If is capture the flag game mode
            if (gameMode instanceof CaptureTheFlagGameMode ctfGameMode) {

                // Place the flags
                ctfGameMode.placeFlags(world, team);

                this.sendTeamFlagCapturedMessage(world, team);
            }
        }
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    private void putTeamToSpectators(ServerWorld world, TeamDto team) {

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var teamManager = gameManager.getTeamManager();

        teamManager.getPlayersOfTeam(team).forEach(playerUuid -> {

            // Get team of the flag the player is currently holding
            var teamOptional = this.getPlayerHoldingFlagTeam(playerUuid);

            // Drop flag
            teamOptional.ifPresent(t -> this.playerDropFlag(world, playerUuid));

            // Get the player
            var player = world.getPlayerByUuid(playerUuid);

            // Sanity check
            if (player == null) {
                return;
            }

            // Cast to server player entity
            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return;
            }

            // Set player to spectator game mode
            serverPlayer.changeGameMode(GameMode.SPECTATOR);
        });
    }

    private void notifyPlayersAboutFlagCountUpdate(ServerWorld world, TeamDto team, long newNumberOfFlags) {
        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeInt(team.id());
        buffer.writeLong(newNumberOfFlags);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.CTF_NUMBER_OF_FLAGS_UPDATE, buffer);
    }

    private void notifyPlayersAboutFlagHoldingUpdate(ServerWorld world, UUID playerUuid, Integer teamId) {
        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeUuid(playerUuid);

        var teamIdString = "null";
        if (teamId != null) {
            teamIdString = String.valueOf(teamId);
        }
        buffer.writeString(teamIdString);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.CTF_FLAG_HOLDING_UPDATE, buffer);
    }

    private void sendTeamFlagStolenMessage(ServerWorld world, TeamDto team) {

        boolean sendMessage = LasertagGameManager.getInstance().getSettingsManager().get(SettingDescription.SEND_FLAG_STOLEN_MESSAGE);
        if (!sendMessage) {
            return;
        }

        var msg = Text.literal("Flag of Team ");
        var teamName = Text.literal(team.name()).setStyle(Style.EMPTY.withColor(team.color().getValue()));
        msg.append(teamName);
        msg.append(" got stolen!");
        world.getServer().getPlayerManager().broadcast(msg, false);
    }

    private void sendTeamFlagCapturedMessage(ServerWorld world, TeamDto team) {

        boolean sendMessage = LasertagGameManager.getInstance().getSettingsManager().get(SettingDescription.SEND_FLAG_CAPTURED_MESSAGE);
        if (!sendMessage) {
            return;
        }

        var msg = Text.literal("Flag of Team ");
        var teamName = Text.literal(team.name()).setStyle(Style.EMPTY.withColor(team.color().getValue()));
        msg.append(teamName);
        msg.append(" got captured!");
        world.getServer().getPlayerManager().broadcast(msg, false);
    }

    private void sendTeamIsOutMessage(ServerWorld world, TeamDto team) {

        boolean sendMessage = LasertagGameManager.getInstance().getSettingsManager().get(SettingDescription.SEND_TEAM_OUT_MESSAGE);
        if (!sendMessage) {
            return;
        }

        var msg = Text.literal("Team ");
        var teamName = Text.literal(team.name()).setStyle(Style.EMPTY.withColor(team.color().getValue()));
        msg.append(teamName);
        msg.append(" lost all its flags!");
        world.getServer().getPlayerManager().broadcast(msg, false);
    }
}
