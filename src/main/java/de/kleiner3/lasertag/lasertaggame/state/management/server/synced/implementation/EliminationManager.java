package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.client.SoundEvents;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IEliminationManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ITeamsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.IEliminationState;
import de.kleiner3.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.TeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.UIState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the IElimination manager for the lasertag game
 *
 * @author Étienne Muser
 */
public class EliminationManager implements IEliminationManager {

    /**
     * The number of lasertag ticks since the last phase change.
     * Initialize with -1 as there will be a tick at 0 seconds
     * into the game.
     */
    private long ticksSinceLastPhaseChange = -1L;

    private final MinecraftServer server;

    private final IEliminationState eliminationState;

    private final ISettingsManager settingsManager;

    private final ITeamsManager teamsManager;

    private final UIState uiState;

    private final ITeamsConfigState teamsConfigState;

    public EliminationManager(MinecraftServer server,
                              IEliminationState eliminationState,
                              ISettingsManager settingsManager,
                              ITeamsManager teamsManager,
                              UIState uiState,
                              ITeamsConfigState teamsConfigState) {
        this.server = server;
        this.eliminationState = eliminationState;
        this.settingsManager = settingsManager;
        this.teamsManager = teamsManager;
        this.uiState = uiState;
        this.teamsConfigState = teamsConfigState;
    }

    @Override
    public synchronized void eliminatePlayer(UUID eliminatedPlayerUuid, UUID shooterUuid, boolean playEliminationSound) {

        LasertagMod.LOGGER.info("Trying to eliminate player '" + server.getOverworld().getPlayerByUuid(eliminatedPlayerUuid).getDisplayName().getString() + "'");

        // If the player is already eliminated
        if (eliminationState.isPlayerEliminated(eliminatedPlayerUuid)) {

            LasertagMod.LOGGER.info("Player is already eliminated.");

            // Do nothing
            return;
        }

        LasertagMod.LOGGER.info("Eliminating player '" + server.getOverworld().getPlayerByUuid(eliminatedPlayerUuid).getDisplayName().getString() + "'");

        // Eliminate the player
        eliminationState.eliminatePlayer(eliminatedPlayerUuid);

        // Get the survive time
        var surviveTime = uiState.gameTime;

        // Put the players survive time
        eliminationState.setPlayerSurviveTime(eliminatedPlayerUuid, surviveTime);

        var newEliminationCount = 0L;

        // If there is a shooter
        if (shooterUuid != null) {

            // Get the shooters elimination count
            var oldEliminationCount = eliminationState.getEliminationCount(shooterUuid);

            // Increase the elimination count
            newEliminationCount = oldEliminationCount + 1;
            eliminationState.setEliminationCount(shooterUuid, newEliminationCount);
        }

        // Check if the players team got eliminated
        checkTeamElimination(eliminatedPlayerUuid, surviveTime);

        // Put the player into spectator game mode
        putPlayerToSpectatorGameMode(eliminatedPlayerUuid);

        // Send the network event
        sendPlayerEliminatedNetworkEvent(eliminatedPlayerUuid, shooterUuid, newEliminationCount, surviveTime);

        // If the elimination sound should be played
        if (playEliminationSound) {

            playEliminationSound();
        }
    }

    @Override
    public synchronized void eliminateTeam(TeamDto team) {

        LasertagMod.LOGGER.info("Eliminating entire team '" + team.name() + "'.");

        // Get the players of the team
        var playersOfTeam = teamsManager.getPlayersOfTeam(team);

        var uuidsString = playersOfTeam.stream().map(UUID::toString).collect(Collectors.joining(", "));

        LasertagMod.LOGGER.info("Current players of team: [" + uuidsString + "]");

        // Eliminate every player of that team
        playersOfTeam.forEach(playerUuid -> eliminatePlayer(playerUuid, null, false));

        playEliminationSound();
    }

    @Override
    public synchronized boolean isPlayerNotEliminated(UUID playerUuid) {
        return !eliminationState.isPlayerEliminated(playerUuid);
    }

    @Override
    public synchronized boolean isTeamNotEliminated(TeamDto team) {
        return !eliminationState.isTeamEliminated(team.id());
    }

    @Override
    public synchronized long getPlayerEliminationCount(UUID playerUuid) {
        return eliminationState.getEliminationCount(playerUuid);
    }

    @Override
    public synchronized void reset() {
        eliminationState.reset();

        ServerEventSending.sendToEveryone(server, NetworkingConstants.ELIMINATION_STATE_RESET, PacketByteBufs.empty());
    }

    @Override
    public synchronized void tick() {

        // Increment time since last phase change
        ++ticksSinceLastPhaseChange;

        // If the phase is now over
        if (ticksSinceLastPhaseChange >= settingsManager.<Long>get(SettingDescription.PHASE_DURATION)) {

            // Reset the time since last phase change
            ticksSinceLastPhaseChange = 0L;

            // Shrink the border
            shrinkBorder();
        }
    }

    @Override
    public synchronized Long getTeamSurviveTime(TeamDto team) {
        return eliminationState.getTeamSurviveTime(team.id());
    }

    @Override
    public synchronized Long getPlayerSurviveTime(UUID playerUuid) {
        return eliminationState.getPlayerSuriviveTime(playerUuid);
    }

    @Override
    public synchronized List<Integer> getRemainingTeamIds() {

        return teamsConfigState.getTeams().stream()
                .filter(team -> !teamsManager.getPlayersOfTeam(team).isEmpty())
                .filter(team -> !team.equals(TeamsConfigState.SPECTATORS))
                .map(TeamDto::id)
                .filter(id -> !eliminationState.isTeamEliminated(id))
                .toList();
    }

    private void checkTeamElimination(UUID playerUuid, long surviveTime) {

        // Get the players team
        var eliminatedPlayersTeam = teamsManager.getTeamOfPlayer(playerUuid).orElseThrow();

        // Check if the team has still players left
        var teamHasPlayersLeft = teamsManager.getPlayersOfTeam(eliminatedPlayersTeam).stream()
                .anyMatch(this::isPlayerNotEliminated);

        // If team is now eliminated (has no players left)
        if (!teamHasPlayersLeft) {

            LasertagMod.LOGGER.info("Team '" + eliminatedPlayersTeam.name() + "' has no players left... Team eliminated.");

            setTeamEliminated(eliminatedPlayersTeam, surviveTime);
        }
    }

    private void setTeamEliminated(TeamDto team, long surviveTime) {

        eliminationState.setTeamSurviveTime(team.id(), surviveTime);
        eliminationState.eliminateTeam(team.id());
        sendTeamIsOutMessage(team);
        sendTeamEliminatedNetworkEvent(team, surviveTime);
    }

    private void putPlayerToSpectatorGameMode(UUID playerUuid) {

        // Get the eliminated player
        var player = server.getOverworld().getPlayerByUuid(playerUuid);

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
    }

    private void sendPlayerEliminatedNetworkEvent(UUID eliminatedPlayerUuid,
                                                  UUID shooterUuid,
                                                  long newEliminationCount,
                                                  long surviveTime) {

        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeUuid(eliminatedPlayerUuid);
        buf.writeNullable(shooterUuid, PacketByteBuf::writeUuid);
        buf.writeLong(newEliminationCount);
        buf.writeLong(surviveTime);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.PLAYER_ELIMINATED, buf);
    }

    private void sendTeamEliminatedNetworkEvent(TeamDto team,
                                                long surviveTime) {
        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(team.id());
        buf.writeLong(surviveTime);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.TEAM_ELIMINATED, buf);
    }

    private void shrinkBorder() {

        // Get the world border
        var worldBorder = server.getOverworld().getWorldBorder();

        // Get the shrink distance
        var shrinkDistance = settingsManager.<Long>get(SettingDescription.BORDER_SHRINK_DISTANCE) * 2;

        // Get the shrink time
        var shrinkTime = settingsManager.<Long>get(SettingDescription.BORDER_SHRINK_TIME);

        // Get the current border size
        var currentBorderSize = worldBorder.getSize();

        // Calculate the new border size
        var newBorderSize = Math.max(0, currentBorderSize - shrinkDistance);

        // If the border should not shrink
        if (newBorderSize == currentBorderSize) {
            return;
        }

        // Shrink the border
        if (shrinkTime > 0L) {

            // Calculate totalShrinkTime
            var totalShrinkTime = shrinkTime * 1000L;

            worldBorder.interpolateSize(currentBorderSize, newBorderSize, totalShrinkTime);
        } else {

            worldBorder.setSize(newBorderSize);
        }
    }

    private void sendTeamIsOutMessage(TeamDto team) {

        boolean sendMessage = settingsManager.get(SettingDescription.SEND_TEAM_OUT_MESSAGE);

        if (!sendMessage) {
            return;
        }

        var msg = Text.literal("Team ");
        var teamName = Text.literal(team.name()).setStyle(Style.EMPTY.withColor(team.color().getValue()));
        msg.append(teamName);
        msg.append(" got eliminated!");
        server.getPlayerManager().broadcast(msg, false);
    }

    private void playEliminationSound() {

        LasertagMod.LOGGER.info("Playing elimination sound...");

        server.getOverworld().playSound(null,
                BlockPos.ORIGIN,
                SoundEvents.PLAYER_ELIMINATION_SOUND_EVENT,
                SoundCategory.AMBIENT,
                200.0f,
                1.0f);
    }
}
