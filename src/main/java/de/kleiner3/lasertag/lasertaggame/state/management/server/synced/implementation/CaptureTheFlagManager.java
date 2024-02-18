package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.lasertaggame.gamemode.implementation.CaptureTheFlagGameMode;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.settings.valuetypes.CTFFlagHoldingPlayerVisibility;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ICaptureTheFlagManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IGameModeManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ITeamsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.ICaptureTheFlagState;
import de.kleiner3.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.TeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.UIState;
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
import net.minecraft.world.GameMode;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the ICaptureTheFlagManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class CaptureTheFlagManager implements ICaptureTheFlagManager {

    private final ICaptureTheFlagState captureTheFlagState;
    private final UIState uiState;

    private final ISettingsManager settingsManager;
    private final IGameModeManager gameModeManager;
    private final ITeamsManager teamsManager;
    private final ITeamsConfigState teamsConfigState;

    private final ServerWorld world;

    public CaptureTheFlagManager(ServerWorld world,
                                 ICaptureTheFlagState captureTheFlagState,
                                 ISettingsManager settingsManager,
                                 IGameModeManager gameModeManager,
                                 UIState uiState,
                                 ITeamsManager teamsManager,
                                 ITeamsConfigState teamsConfigState) {
        this.captureTheFlagState = captureTheFlagState;
        this.settingsManager = settingsManager;
        this.gameModeManager = gameModeManager;
        this.uiState = uiState;
        this.teamsManager = teamsManager;
        this.world = world;
        this.teamsConfigState = teamsConfigState;
    }

    @Override
    public void reset() {
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
    public void playerDropFlag(UUID playerUuid) {

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
    public void playerPickupFlag(ServerPlayerEntity player, TeamDto team) {

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
    public Optional<TeamDto> getPlayerHoldingFlagTeam(UUID playerUuid) {

        return captureTheFlagState.getPlayerHoldingFlagTeam(playerUuid).map(t -> teamsConfigState.getTeamOfId(t).orElseThrow());
    }

    @Override
    public long getNumberOfCapturedFlags(UUID playerUuid) {
        return captureTheFlagState.getNumberOfCapturedFlags(playerUuid);
    }

    @Override
    public Optional<Long> getSurviveTime(TeamDto team) {
        return captureTheFlagState.getSurviveTime(team);
    }

    @Override
    public long getNumberOfFlags(TeamDto teamDto) {
        return captureTheFlagState.getNumberOfFlags(teamDto);
    }

    @Override
    public void flagCaptured(UUID playerUuid, TeamDto team) {

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

            captureTheFlagState.setSurviveTime(team, uiState.gameTime);
            sendTeamIsOutMessage(team);
            putTeamToSpectators(team);
            gameMode.checkGameOver(world.getServer());
        } else {

            // If is capture the flag game mode
            if (gameMode instanceof CaptureTheFlagGameMode ctfGameMode) {

                // Place the flags
                ctfGameMode.placeFlags(world, team);

                sendTeamFlagCapturedMessage(team);
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

        var msg = Text.literal("Flag of Team ");
        var teamName = Text.literal(team.name()).setStyle(Style.EMPTY.withColor(team.color().getValue()));
        msg.append(teamName);
        msg.append(" got stolen!");
        world.getServer().getPlayerManager().broadcast(msg, false);
    }

    private void notifyPlayersAboutFlagCountUpdate(TeamDto team, long newNumberOfFlags) {
        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeInt(team.id());
        buffer.writeLong(newNumberOfFlags);

        ServerEventSending.sendToEveryone(world.getServer(), NetworkingConstants.CTF_NUMBER_OF_FLAGS_UPDATE, buffer);
    }

    private void sendTeamIsOutMessage(TeamDto team) {

        boolean sendMessage = settingsManager.get(SettingDescription.SEND_TEAM_OUT_MESSAGE);
        if (!sendMessage) {
            return;
        }

        var msg = Text.literal("Team ");
        var teamName = Text.literal(team.name()).setStyle(Style.EMPTY.withColor(team.color().getValue()));
        msg.append(teamName);
        msg.append(" lost all its flags!");
        world.getServer().getPlayerManager().broadcast(msg, false);
    }

    private void putTeamToSpectators(TeamDto team) {

        teamsManager.getPlayersOfTeam(team).forEach(playerUuid -> {

            // Get team of the flag the player is currently holding
            var teamOptional = this.getPlayerHoldingFlagTeam(playerUuid);

            // Drop flag
            teamOptional.ifPresent(t -> this.playerDropFlag(playerUuid));

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

    private void sendTeamFlagCapturedMessage(TeamDto team) {

        boolean sendMessage = settingsManager.get(SettingDescription.SEND_FLAG_CAPTURED_MESSAGE);
        if (!sendMessage) {
            return;
        }

        var msg = Text.literal("Flag of Team ");
        var teamName = Text.literal(team.name()).setStyle(Style.EMPTY.withColor(team.color().getValue()));
        msg.append(teamName);
        msg.append(" got captured!");
        world.getServer().getPlayerManager().broadcast(msg, false);
    }
}
