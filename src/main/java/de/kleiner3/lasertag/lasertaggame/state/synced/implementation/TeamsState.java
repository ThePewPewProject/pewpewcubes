package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.state.synced.ITeamsState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import de.kleiner3.lasertag.lasertaggame.team.serialize.TeamConfigManagerDeserializer;
import de.kleiner3.lasertag.lasertaggame.team.serialize.TeamDtoSerializer;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Implementation of ITeamsState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class TeamsState implements ITeamsState {

    /**
     * Map every teams id to the set of uuids of the players this team contains
     */
    private final HashMap<Integer, Set<UUID>> teamMap = new HashMap<>();

    /**
     * Map every players uuid to the team id of the team they are in
     */
    private final HashMap<UUID, Integer> playerTeamMap = new HashMap<>();

    public TeamsState(ITeamsConfigState teamsConfigState) {

        // Initialize team map
        for (TeamDto teamDto : teamsConfigState.getTeams()) {

            teamMap.put(teamDto.id(), new HashSet<>());
        }
    }

    @Override
    public synchronized void updateTeamOfPlayer(UUID playerUuid, TeamDto newTeamDto) {

        // Get the id of the team the player is currently in
        var oldTeamIdOptional = Optional.ofNullable(playerTeamMap.get(playerUuid));

        // If the player is already in the team
        if (oldTeamIdOptional.isPresent() && oldTeamIdOptional.get() == newTeamDto.id()) {

            // Nothing to do
            return;
        }

        // Set the player to the new team
        playerTeamMap.put(playerUuid, newTeamDto.id());
        teamMap.get(newTeamDto.id()).add(playerUuid);

        // Remove the player from the old team, if he was in a team
        oldTeamIdOptional.ifPresent(oldTeamId -> teamMap.get(oldTeamId).remove(playerUuid));
    }

    @Override
    public synchronized void removePlayerFromTeam(UUID playerUuid) {

        // Get the id of the team the player is currently in
        var oldTeamIdOptional = Optional.ofNullable(playerTeamMap.get(playerUuid));

        // If the player was not in a team
        if (oldTeamIdOptional.isEmpty()) {

            // Nothing to do
            return;
        }

        // Extract the old team id from the optional
        var oldTeamId = oldTeamIdOptional.get();

        // Remove the player from his team
        playerTeamMap.remove(playerUuid);
        teamMap.get(oldTeamId).remove(playerUuid);
    }

    @Override
    public synchronized List<UUID> getPlayersOfTeam(TeamDto team) {
        return teamMap.get(team.id()).stream().toList();
    }

    @Override
    public synchronized Optional<Integer> getTeamOfPlayer(UUID playerUuid) {
        return Optional.ofNullable(playerTeamMap.get(playerUuid));
    }

    @Override
    public synchronized void forEachPlayer(BiConsumer<Integer, UUID> callback) {

        for (var entry : teamMap.entrySet()) {

            for (var playerUuid : entry.getValue()) {

                callback.accept(entry.getKey(), playerUuid);
            }
        }
    }

    @Override
    public synchronized boolean isPlayerInTeam(UUID playerUuid) {
        return playerTeamMap.containsKey(playerUuid);
    }

    @Override
    public synchronized void reset(ITeamsConfigState teamsConfigState) {

        teamMap.clear();

        for (TeamDto teamDto : teamsConfigState.getTeams()) {

            teamMap.put(teamDto.id(), new HashSet<>());
        }

        playerTeamMap.clear();
    }

    @Override
    public String toJson() {
        var builder = new GsonBuilder();

        // Register team serializer
        builder.registerTypeAdapter(TeamDto.class, TeamDtoSerializer.getSerializer());

        return builder.create().toJson(this);
    }

    public static TeamsState fromJson(String jsonString) {
        var builder = new GsonBuilder();

        // Register team serializer
        builder.registerTypeAdapter(new TypeToken<HashMap<String, TeamDto>>() {}.getType(), TeamConfigManagerDeserializer.getDeserializer());

        return builder.create().fromJson(jsonString, TeamsState.class);
    }
}
