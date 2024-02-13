package de.kleiner3.lasertag.lasertaggame.state.management.client.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.kleiner3.lasertag.lasertaggame.state.management.client.IRemainingTeamsManager;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.List;

/**
 * Implementation of IRemainingTeamsManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class RemainingTeamsManager implements IRemainingTeamsManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void removeTeam(int teamId) {
        clientManager.getSyncedState().getRemainingTeamsState().removeTeam(teamId);
    }

    @Override
    public List<Integer> getRemainingTeamIds() {
        return clientManager.getSyncedState().getRemainingTeamsState().getRemainingTeams().stream().toList();
    }

    @Override
    public void reset() {

        var teamsManager = clientManager.getTeamsManager();
        var teamsConfigState = clientManager.getSyncedState().getTeamsConfigState();

        var teams = teamsConfigState.getTeams().stream()
                .filter(team -> !teamsManager.getPlayersOfTeam(team).isEmpty())
                .map(TeamDto::id)
                .toList();
        clientManager.getSyncedState().getRemainingTeamsState().reset(teams);
    }
}
