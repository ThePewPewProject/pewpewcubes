package de.pewpewproject.lasertag.lasertaggame.state.management.client.implementation;

import de.pewpewproject.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.ITeamsManager;
import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ITeamsManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class TeamsManager implements ITeamsManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public List<UUID> getPlayersOfTeam(TeamDto team) {
        return clientManager.getSyncedState().getTeamsState().getPlayersOfTeam(team);
    }

    @Override
    public void updateTeamOfPlayer(UUID playerUuid, TeamDto newTeam) {

        if (newTeam == null) {
            clientManager.getSyncedState().getTeamsState().removePlayerFromTeam(playerUuid);
        } else {
            clientManager.getSyncedState().getTeamsState().updateTeamOfPlayer(playerUuid, newTeam);
        }
    }

    @Override
    public void removePlayerFromTeam(UUID playerUuid) {
        clientManager.getSyncedState().getTeamsState().removePlayerFromTeam(playerUuid);
    }

    @Override
    public boolean isPlayerInTeam(UUID playerUuid) {
        return clientManager.getSyncedState().getTeamsState().isPlayerInTeam(playerUuid);
    }

    @Override
    public Optional<Integer> getTeamOfPlayer(UUID playerUuid) {
        return clientManager.getSyncedState().getTeamsState().getTeamOfPlayer(playerUuid);
    }
}
