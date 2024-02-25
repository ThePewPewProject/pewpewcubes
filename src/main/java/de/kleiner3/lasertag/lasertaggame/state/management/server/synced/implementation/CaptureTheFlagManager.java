package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.lasertaggame.gamemode.implementation.CaptureTheFlagGameMode;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.settings.valuetypes.CTFFlagHoldingPlayerVisibility;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.*;
import de.kleiner3.lasertag.lasertaggame.state.synced.ICaptureTheFlagState;
import de.kleiner3.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.TeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
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

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the ICaptureTheFlagManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class CaptureTheFlagManager implements ICaptureTheFlagManager {

    private final ICaptureTheFlagState captureTheFlagState;

    private final ISettingsManager settingsManager;
    private final IGameModeManager gameModeManager;
    private final ITeamsManager teamsManager;
    private final IEliminationManager eliminationManager;
    private final ITeamsConfigState teamsConfigState;

    private final ServerWorld world;

    public CaptureTheFlagManager(ServerWorld world,
                                 ICaptureTheFlagState captureTheFlagState,
                                 ISettingsManager settingsManager,
                                 IGameModeManager gameModeManager,
                                 ITeamsManager teamsManager,
                                 IEliminationManager eliminationManager,
                                 ITeamsConfigState teamsConfigState) {
        this.captureTheFlagState = captureTheFlagState;
        this.settingsManager = settingsManager;
        this.gameModeManager = gameModeManager;
        this.teamsManager = teamsManager;
        this.world = world;
        this.eliminationManager = eliminationManager;
        this.teamsConfigState = teamsConfigState;
    }

    @Override
    public synchronized void reset() {
        captureTheFlagState.reset();

        // For every team that is not empty and not spectators
        teamsConfigState
                .getTeams()
                .stream()
                .filter(team -> !teamsManager.getPlayersOfTeam(team).isEmpty())
                .filter(team -> !team.equals(TeamsConfigState.SPECTATORS))
                .forEach(team -> {
                    // Put the teams initial flag count
                    captureTheFlagState.updateTeamFlagCount(team, settingsManager.<Long>get(SettingDescription.FLAG_COUNT));
                });

        ServerEventSending.sendToEveryone(world.getServer(), NetworkingConstants.FLAG_RESET, PacketByteBufs.empty());
    }

    @Override
    public synchronized void playerDropFlag(UUID playerUuid) {

        // Get the team of the flag the player is holding
        var flagTeamOptional = captureTheFlagState.getPlayerHoldingFlagTeam(playerUuid);

        // If player is not holding a flag
        if (flagTeamOptional.isEmpty()) {
            // Do nothing
            return;
        }

        // Remove the flag from the player
        captureTheFlagState.playerDropFlag(playerUuid);

        // Get the player
        var player = world.getPlayerByUuid(playerUuid);

        // Sanity check
        if (player != null) {

            // Take flag from players inventory
            Inventories.remove(player.getInventory(), stack -> stack.isOf(Items.LASERTAG_FLAG), 1, false);
        }

        // If flag holding players should glow
        if (settingsManager.getEnum(SettingDescription.CTF_FLAG_HOLDING_PLAYER_VISIBILITY) == CTFFlagHoldingPlayerVisibility.GLOW) {

            // Sanity check
            if (player != null) {

                // Set player not glowing
                player.setGlowing(false);
            }
        }

        notifyPlayersAboutFlagHoldingUpdate(playerUuid, null);

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        if (!(gameMode instanceof CaptureTheFlagGameMode ctfGameMode)) {
            return;
        }

        // Place the flags
        flagTeamOptional.ifPresent(team -> ctfGameMode.placeFlags(world, teamsConfigState.getTeamOfId(team).orElseThrow()));
    }

    @Override
    public synchronized void playerPickupFlag(ServerPlayerEntity player, TeamDto team) {

        // Send flag stolen message
        sendTeamFlagStolenMessage(team);

        // Give the player the flag
        captureTheFlagState.playerPickupFlag(player.getUuid(), team.id());

        // If flag holding players should glow
        if (settingsManager.getEnum(SettingDescription.CTF_FLAG_HOLDING_PLAYER_VISIBILITY) == CTFFlagHoldingPlayerVisibility.GLOW) {

            // Set player glowing
            player.setGlowing(true);
        }

        this.notifyPlayersAboutFlagHoldingUpdate(player.getUuid(), team.id());
    }

    @Override
    public synchronized Optional<TeamDto> getPlayerHoldingFlagTeam(UUID playerUuid) {

        return captureTheFlagState.getPlayerHoldingFlagTeam(playerUuid).map(t -> teamsConfigState.getTeamOfId(t).orElseThrow());
    }

    @Override
    public synchronized long getNumberOfCapturedFlags(UUID playerUuid) {
        return captureTheFlagState.getNumberOfCapturedFlags(playerUuid);
    }

    @Override
    public synchronized long getNumberOfFlags(TeamDto teamDto) {
        return captureTheFlagState.getNumberOfFlags(teamDto);
    }

    @Override
    public synchronized void flagCaptured(UUID playerUuid, TeamDto team) {

        // Get the teams number of flags
        var numberOfFlags = captureTheFlagState.getNumberOfFlags(team);

        // Remove one flag
        --numberOfFlags;

        // Save the new number of flags
        captureTheFlagState.updateTeamFlagCount(team, numberOfFlags);

        // Get the old number of flags captured
        var oldFlagCount = captureTheFlagState.getNumberOfCapturedFlags(playerUuid);

        // Increase
        var newFlagCaptureCount = oldFlagCount + 1;

        // Save the new flag capture sound
        captureTheFlagState.setNumberOfCapturedFlags(playerUuid, newFlagCaptureCount);

        // Set the player is not holding a flag
        captureTheFlagState.playerDropFlag(playerUuid);

        notifyPlayersAboutFlagCountUpdate(team, numberOfFlags);
        notifyPlayersAboutFlagHoldingUpdate(playerUuid, null);

        // Get the current game mode
        var gameMode = gameModeManager.getGameMode();

        if (numberOfFlags == 0) {

            LasertagMod.LOGGER.info("[CTF] Team " + team.name() + " got eliminated...");

            // Eliminate team
            eliminationManager.eliminateTeam(team);

            // Drop the flags every player is holding
            teamsManager.getPlayersOfTeam(team).forEach(this::playerDropFlag);

            // Check game over
            gameMode.checkGameOver(world.getServer());
        } else {

            LasertagMod.LOGGER.info("[CTF] Team " + team.name() + " lost a flag...");

            // If is capture the flag game mode
            if (gameMode instanceof CaptureTheFlagGameMode ctfGameMode) {

                // Place the flags
                ctfGameMode.placeFlags(world, team);

                // Get the capturing teams name
                var capturingTeam = teamsManager.getTeamOfPlayer(playerUuid).orElseThrow();

                sendTeamFlagCapturedMessage(team, capturingTeam);
            }
        }
    }

    private void notifyPlayersAboutFlagHoldingUpdate(UUID playerUuid, Integer teamId) {
        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeUuid(playerUuid);

        var teamIdString = "null";
        if (teamId != null) {
            teamIdString = String.valueOf(teamId);
        }
        buffer.writeString(teamIdString);

        ServerEventSending.sendToEveryone(world.getServer(), NetworkingConstants.CTF_FLAG_HOLDING_UPDATE, buffer);
    }

    private void sendTeamFlagStolenMessage(TeamDto team) {

        boolean sendMessage = settingsManager.get(SettingDescription.SEND_FLAG_STOLEN_MESSAGE);
        if (!sendMessage) {
            return;
        }

        // Get the team name as text
        var teamName = Text.literal(team.name()).setStyle(Style.EMPTY.withColor(team.color().getValue()));

        // Build the message
        var msg = Text.translatable("chat.message.flag_stolen", teamName);

        world.getServer().getPlayerManager().broadcast(msg, false);
    }

    private void notifyPlayersAboutFlagCountUpdate(TeamDto team, long newNumberOfFlags) {
        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeInt(team.id());
        buffer.writeLong(newNumberOfFlags);

        ServerEventSending.sendToEveryone(world.getServer(), NetworkingConstants.CTF_NUMBER_OF_FLAGS_UPDATE, buffer);
    }

    private void sendTeamFlagCapturedMessage(TeamDto losingTeam, TeamDto capturingTeam) {

        boolean sendMessage = settingsManager.get(SettingDescription.SEND_FLAG_CAPTURED_MESSAGE);
        if (!sendMessage) {
            return;
        }

        // Get the losing teams name
        var losingTeamName = Text.literal(losingTeam.name()).setStyle(Style.EMPTY.withColor(losingTeam.color().getValue()));

        // Get the capturing teams name
        var capturingTeamName = Text.literal(capturingTeam.name()).setStyle(Style.EMPTY.withColor(capturingTeam.color().getValue()));

        // Build the message
        var msg = Text.translatable("chat.message.flag_captured", losingTeamName, capturingTeamName);

        world.getServer().getPlayerManager().broadcast(msg, false);
    }
}
