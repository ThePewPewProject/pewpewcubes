package de.kleiner3.lasertag.lasertaggame.management.team;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamConfigManagerDeserializer;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamDtoSerializer;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to manage the lasertag teams
 *
 * @author Étienne Muser
 */
public class LasertagTeamManager implements IManager {

    private HashMap<Integer, Set<UUID>> teamMap;
    private HashMap<UUID, Integer> playerTeamMap;

    private TeamConfigManager teamConfigManager;

    public LasertagTeamManager() {
        teamConfigManager = new TeamConfigManager();

        // Initialize team map
        teamMap = new HashMap<>();

        for (TeamDto teamDto : teamConfigManager.teamConfig.values()) {

            teamMap.put(teamDto.id(), new HashSet<>());
        }

        playerTeamMap = new HashMap<>();
    }

    public TeamConfigManager getTeamConfigManager() {
        return teamConfigManager;
    }

    public List<UUID> getPlayersOfTeam(TeamDto team) {
        return teamMap.get(team.id()).stream().toList();
    }

    /**
     * Executes the given callback for every player currently registered.
     *
     * @param callback
     */
    public void forEachPlayer(IForEachPlayerCallback callback) {

        for (var entry : teamMap.entrySet()) {

            for (var playerUuid : entry.getValue()) {

                callback.execute(teamConfigManager.getTeamOfId(entry.getKey()).get(), playerUuid);
            }
        }
    }

    public Map<TeamDto, List<UUID>> getTeamMap() {
        return teamMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> teamConfigManager.getTeamOfId(entry.getKey()).get(), entry -> entry.getValue().stream().toList()));
    }

    /**
     * Updates the team map
     *
     * @param playerUuid The player who changed team
     * @param oldTeam The old team of the player
     * @param newTeam The new team of the player
     */
    public void updateTeam(UUID playerUuid, TeamDto oldTeam, TeamDto newTeam) {

        // Update team map
        if (oldTeam != null) {
            teamMap.get(oldTeam.id()).remove(playerUuid);
        }
        if (newTeam != null) {
            teamMap.get(newTeam.id()).add(playerUuid);
        }

        // Update player map
        if (newTeam != null) {
            playerTeamMap.put(playerUuid, newTeam.id());
        } else {
            playerTeamMap.remove(playerUuid);
        }
    }

    /**
     * Add a player to the given team
     *
     * @param newTeamDto The team to join
     * @param player The player to join the team
     * @return True if the join succeeded. Otherwise false.
     */
    public boolean playerJoinTeam(ServerWorld world, TeamDto newTeamDto, PlayerEntity player) {

        // Get new team
        var newTeam = teamMap.get(newTeamDto.id());

        // Check if team is full
        if (newTeam.size() >= LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.MAX_TEAM_SIZE)) {
            // If is Server
            if (player instanceof ServerPlayerEntity) {
                ServerEventSending.sendErrorMessageToClient((ServerPlayerEntity) player, "Team " + newTeamDto.name() + " is full.");
            }
            return false;
        }

        // Check if player is in a team already
        TeamDto oldTeamDto = null;
        for (var t : teamConfigManager.teamConfig.values()) {

            var team = teamMap.get(t.id());

            if (team.contains(player.getUuid())) {
                oldTeamDto = t;
                break;
            }
        }

        // If player has no team
        if (oldTeamDto == null) {
            teamMap.get(newTeamDto.id()).add(player.getUuid());
        } else {
            // If player tries to join his team again
            if (newTeamDto == oldTeamDto) {
                return true;
            }

            // Swap team
            teamMap.get(oldTeamDto.id()).remove(player.getUuid());
            teamMap.get(newTeamDto.id()).add(player.getUuid());
        }

        // Uptade player-team cache
        playerTeamMap.put(player.getUuid(), newTeamDto.id());

        // Get players inventory
        var inventory = player.getInventory();

        // Clear players inventory
        inventory.clear();

        if (!newTeamDto.equals(TeamConfigManager.SPECTATORS)) {

            // Give player a lasertag vest
            var vestStack = new ItemStack(Items.LASERTAG_VEST);
            vestStack.setHolder(player);
            player.equipStack(EquipmentSlot.CHEST, vestStack);

            // Give player a lasertag weapon
            var weaponStack = new ItemStack(Items.LASERTAG_WEAPON);
            weaponStack.setHolder(player);
            inventory.insertStack(0, weaponStack);
        }

        // Notify about change
        notifyPlayersAboutUpdate(world, player.getUuid(), oldTeamDto, newTeamDto);

        return true;
    }

    /**
     * Force remove a player from his team
     *
     * @param player The player to leave his team
     */
    public void playerLeaveHisTeam(ServerWorld world, PlayerEntity player) {

        playerLeaveHisTeam(world, player.getUuid());
    }

    /**
     * Force remove a player from his team
     *
     * @param playerUuid The id of the player to leave his team
     */
    public void playerLeaveHisTeam(ServerWorld world, UUID playerUuid) {

        // For each team
        for (var team : teamMap.entrySet()) {

            // If the player is in the team
            if (team.getValue().contains(playerUuid)) {

                // Leave the team
                team.getValue().remove(playerUuid);

                // Uptade player-team cache
                playerTeamMap.put(playerUuid, null);

                notifyPlayersAboutUpdate(world, playerUuid, teamConfigManager.getTeamOfId(team.getKey()).get(), null);

                return;
            }
        }
    }

    /**
     * Checks whether a palyer is in a team or not
     *
     * @param uuid The uuid of the player
     * @return If the player is in a team
     */
    public boolean isPlayerInTeam(UUID uuid) {

        return playerTeamMap.containsKey(uuid);
    }

    /**
     * Gets the team of the player or null if he is in no team
     *
     * @param playerUuid The uuid of the player
     * @return
     */
    public Optional<TeamDto> getTeamOfPlayer(UUID playerUuid) {

        var teamId = playerTeamMap.get(playerUuid);

        if (teamId == null) {
            return Optional.empty();
        }

        return teamConfigManager.getTeamOfId(teamId);
    }

    /**
     * Sends the team update to all clients
     */
    public void notifyPlayersAboutUpdate(ServerWorld world, UUID key, TeamDto oldValue, TeamDto newValue) {

        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeUuid(key);

        // Put old value to buffer
        var oldValueJsonString = "null";
        if (oldValue != null) {

            oldValueJsonString = String.valueOf(oldValue.id());
        }
        buffer.writeString(oldValueJsonString);

        // Put new value to buffer
        var newValueJsonString = "null";
        if (newValue != null) {

            newValueJsonString = String.valueOf(newValue.id());
        }
        buffer.writeString(newValueJsonString);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.TEAM_UPDATE, buffer);
    }

    public String toJson() {
        var builder = new GsonBuilder();

        // Register team serializer
        builder.registerTypeAdapter(TeamDto.class, TeamDtoSerializer.getSerializer());

        return builder.create().toJson(this);
    }

    public static LasertagTeamManager fromJson(String jsonString) {
        var builder = new GsonBuilder();

        // Register team serializer
        builder.registerTypeAdapter(new TypeToken<HashMap<String, TeamDto>>() {}.getType(), TeamConfigManagerDeserializer.getDeserializer());

        return builder.create().fromJson(jsonString, LasertagTeamManager.class);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }
}
