package de.kleiner3.lasertag.lasertaggame.state.management.client.implementation;

import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.client.ICaptureTheFlagManager;
import de.kleiner3.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.TeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ICaptureTheFlagManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class CaptureTheFlagManager implements ICaptureTheFlagManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public long getNumberOfFlags(TeamDto team) {
        return clientManager.getSyncedState().getCaptureTheFlagState().getNumberOfFlags(team);
    }

    @Override
    public Optional<TeamDto> getPlayerHoldingFlagTeam(UUID playerUuid) {
        return clientManager.getSyncedState().getCaptureTheFlagState()
                .getPlayerHoldingFlagTeam(playerUuid)
                .map(teamId -> clientManager.getSyncedState().getTeamsConfigState().getTeamOfId(teamId).orElseThrow());
    }

    @Override
    public void updateFlagHolding(UUID playerUuid, int teamId) {
        clientManager.getSyncedState().getCaptureTheFlagState()
                .playerPickupFlag(playerUuid, teamId);
    }

    @Override
    public void removeFlagHolding(UUID playerUuid) {
        clientManager.getSyncedState().getCaptureTheFlagState().playerDropFlag(playerUuid);
    }

    @Override
    public void reset() {

        // Get the game managers
        var syncedState = clientManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();
        var teamsManager = clientManager.getTeamsManager();
        var captureTheFlagState = syncedState.getCaptureTheFlagState();
        var settingsManager = clientManager.getSettingsManager();

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
    }

    @Override
    public void updateTeamFlagCount(TeamDto team, long newNumberOfFlags) {
        clientManager.getSyncedState().getCaptureTheFlagState().updateTeamFlagCount(team, newNumberOfFlags);
    }
}
