package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ITeamsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.state.synced.ITeamsState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.TeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the ITeamsManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class TeamsManager implements ITeamsManager {

    private final ServerWorld world;

    private final ITeamsState teamsState;
    private final ITeamsConfigState teamsConfigState;

    private final ISettingsManager settingsManager;

    public TeamsManager(ITeamsState teamsState, ITeamsConfigState teamsConfigState, ServerWorld world, ISettingsManager settingsManager) {
        this.teamsState = teamsState;
        this.teamsConfigState = teamsConfigState;
        this.world = world;
        this.settingsManager = settingsManager;
    }

    @Override
    public List<UUID> getPlayersOfTeam(TeamDto team) {
        return teamsState.getPlayersOfTeam(team);
    }

    @Override
    public boolean playerJoinTeam(PlayerEntity player, TeamDto newTeamDto) {

        // Get new team
        var newTeam = teamsState.getPlayersOfTeam(newTeamDto);

        // Check if team is full (team size ignored for spectators)
        if (!newTeamDto.equals(TeamsConfigState.SPECTATORS) &&
                newTeam.size() >= settingsManager.<Long>get(SettingDescription.MAX_TEAM_SIZE)) {

            ServerEventSending.sendErrorMessageToClient((ServerPlayerEntity) player, "Team " + newTeamDto.name() + " is full.");
            return false;
        }

        // Update the state
        teamsState.updateTeamOfPlayer(player.getUuid(), newTeamDto);

        // Get players inventory
        var inventory = player.getInventory();

        // Clear players inventory
        inventory.clear();

        if (!newTeamDto.equals(TeamsConfigState.SPECTATORS)) {

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
        notifyPlayersAboutUpdate(player.getUuid(), newTeamDto);

        return true;
    }

    @Override
    public void playerLeaveHisTeam(PlayerEntity player) {

        playerLeaveHisTeam(player.getUuid());
    }

    @Override
    public void playerLeaveHisTeam(UUID playerUuid) {

        teamsState.removePlayerFromTeam(playerUuid);
        notifyPlayersAboutUpdate(playerUuid, null);
    }

    @Override
    public boolean isPlayerInTeam(UUID playerUuid) {
        return teamsState.isPlayerInTeam(playerUuid);
    }

    @Override
    public Optional<TeamDto> getTeamOfPlayer(UUID playerUuid) {
        return teamsState.getTeamOfPlayer(playerUuid).map(teamId -> teamsConfigState.getTeamOfId(teamId).orElseThrow());
    }

    @Override
    public void reloadTeamsConfig() {

        teamsConfigState.reload();

        var teamConfigJson = teamsConfigState.toJson();

        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(teamConfigJson);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.TEAM_CONFIG_RELOADED, buf);
    }

    /**
     * Sends the team update to all clients
     */
    private void notifyPlayersAboutUpdate(UUID key, TeamDto newValue) {

        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeUuid(key);

        // Put new value to buffer
        var newValueJsonString = "null";
        if (newValue != null) {

            newValueJsonString = String.valueOf(newValue.id());
        }
        buffer.writeString(newValueJsonString);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.TEAM_UPDATE, buffer);
    }
}
