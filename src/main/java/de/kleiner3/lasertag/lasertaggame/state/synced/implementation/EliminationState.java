package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.synced.IEliminationState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of IEliminationState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class EliminationState implements IEliminationState {

    /**
     * Map every player to the number of player he has eliminated
     *     key: The players uuid
     *     value: The number of players he eliminated
     */
    private static final Map<UUID, Long> playerEliminationCountMap = new ConcurrentHashMap<>();

    /**
     * Set of all players that have been eliminated
     */
    private static final Set<UUID> eliminatedPlayersSet = new HashSet<>();

    @Override
    public void setEliminationCount(UUID playerUuid, long newCount) {
        playerEliminationCountMap.put(playerUuid, newCount);
    }

    @Override
    public long getEliminationCount(UUID playerUuid) {
        return Optional.ofNullable(playerEliminationCountMap.get(playerUuid)).orElse(0L);
    }

    @Override
    public synchronized void eliminatePlayer(UUID playerUuid) {
        eliminatedPlayersSet.add(playerUuid);
    }

    @Override
    public synchronized boolean isEliminated(UUID playerUuid) {
        return eliminatedPlayersSet.contains(playerUuid);
    }

    @Override
    public synchronized void reset() {
        playerEliminationCountMap.clear();
        eliminatedPlayersSet.clear();
    }
}
