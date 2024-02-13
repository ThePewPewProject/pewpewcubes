package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.synced.IRemainingTeamsState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of IRemainingTeamsState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class RemainingTeamsState implements IRemainingTeamsState {

    private final Set<Integer> remainingTeams = new HashSet<>();

    @Override
    public synchronized Set<Integer> getRemainingTeams() {
        return remainingTeams;
    }

    @Override
    public synchronized void removeTeam(int team) {
        remainingTeams.remove(team);
    }

    @Override
    public boolean remains(int team) {
        return remainingTeams.contains(team);
    }

    @Override
    public synchronized void reset(List<Integer> teamConfig) {
        remainingTeams.clear();
        remainingTeams.addAll(teamConfig);
    }
}
