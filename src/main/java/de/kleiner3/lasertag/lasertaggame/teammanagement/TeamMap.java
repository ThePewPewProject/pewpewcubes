package de.kleiner3.lasertag.lasertaggame.teammanagement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class representing the team map of the lasertag game
 *
 * @author Étienne Muser
 */
public class TeamMap implements Map<TeamDto, List<PlayerEntity>> {
    private final HashMap<TeamDto, List<UUID>> map = new HashMap<>();

    private final MinecraftServer server;

    public TeamMap(MinecraftServer server) {
        this.server = server;
    }

    public void playerJoinTeam(PlayerEntity player, TeamDto team) {
        // Get the team
        var teamList = map.get(team);

        // Add players uuid to team
        teamList.add(player.getUuid());
    }

    public void playerLeaveTeam(PlayerEntity player, TeamDto team) {
        // Get the team
        var teamList = map.get(team);

        // Remove payers uuid from team
        teamList.remove(player.getUuid());
    }

    //region Map implementation

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public List<PlayerEntity> get(Object key) {
        // Get the uuid list
        var uuids = map.get(key);

        // Create return list
        var players = uuidListToPlayerList(uuids);

        return players;
    }

    @Nullable
    @Override
    public List<PlayerEntity> put(TeamDto key, List<PlayerEntity> value) {
        // Convert player list to uuid list
        var uuidList = playerListToUuidList(value);

        // Put
        var uuids = map.put(key, uuidList);

        // Create return list
        var players = uuidListToPlayerList(uuids);

        return players;
    }

    @Override
    public List<PlayerEntity> remove(Object key) {
        // Remove from map
        var uuidList = map.remove(key);

        // Convert uuid list to player list
        var playerList = uuidListToPlayerList(uuidList);

        return playerList;
    }

    @Override
    public void putAll(@NotNull Map<? extends TeamDto, ? extends List<PlayerEntity>> m) {
        // Create hashmap
        var uuidMap = new HashMap<TeamDto, List<UUID>>();

        // For every entry in player map
        for (var playerEntry : m.entrySet()) {
            // Convert player list to uuid list
            var uuidList = playerListToUuidList(playerEntry.getValue());

            // Put in uuid map
            uuidMap.put(playerEntry.getKey(), uuidList);
        }

        map.putAll(uuidMap);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<TeamDto> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<List<PlayerEntity>> values() {
        // Get the values
        var uuidValues = map.values();

        // Create the return collection
        var collection = new LinkedList<List<PlayerEntity>>();

        // For every uuid list in uuidValues
        for (var uuidList : uuidValues) {
            // Convert uuid list to player list
            var playerList = uuidListToPlayerList(uuidList);

            // Add to collection
            collection.add(playerList);
        }

        return collection;
    }

    @NotNull
    @Override
    public Set<Entry<TeamDto, List<PlayerEntity>>> entrySet() {
        // Get the entry set
        var uuidEntrySet = map.entrySet();

        // Create return set
        var set = new HashSet<Entry<TeamDto, List<PlayerEntity>>>();

        // For every entry in entry set
        for (var uuidEntry : uuidEntrySet) {
            // Convert uuid list to player list
            var playerList = uuidListToPlayerList(uuidEntry.getValue());

            // Create new entry
            var playerEntry = new AbstractMap.SimpleEntry<>(uuidEntry.getKey(), playerList);

            // Add to set
            set.add(playerEntry);
        }

        return set;
    }

    //endregion

    //region Private methods

    private List<PlayerEntity> uuidListToPlayerList(List<UUID> uuidList) {
        // Sanity check
        if (uuidList == null) {
            return null;
        }

        // Create player list
        var playerList = new LinkedList<PlayerEntity>();

        // For every uuid
        for (var uuid : uuidList) {
            // Get the player from the server
            var player = server.getPlayerManager().getPlayer(uuid);

            // Add the player to the player list
            playerList.add(player);
        }

        return playerList;
    }

    private List<UUID> playerListToUuidList(List<PlayerEntity> playerList) {
        // Sanity check
        if (playerList == null) {
            return null;
        }

        // Create uuid list
        var uuidList = new LinkedList<UUID>();

        // For every player
        for (var player : playerList) {
            // Get the uuid
            var uuid = player.getUuid();

            // Add the uuid to the uuid list
            uuidList.add(uuid);
        }

        return uuidList;
    }

    //endregion
}
