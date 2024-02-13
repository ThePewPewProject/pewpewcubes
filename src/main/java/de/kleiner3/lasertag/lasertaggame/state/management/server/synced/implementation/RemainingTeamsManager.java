package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IRemainingTeamsManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ITeamsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.IRemainingTeamsState;
import de.kleiner3.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * Implementation of the IRemainingTeamsManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class RemainingTeamsManager implements IRemainingTeamsManager {

    private final IRemainingTeamsState remainingTeamsState;

    private final ITeamsConfigState teamsConfigState;

    private final ITeamsManager teamsManager;

    private final MinecraftServer server;

    public RemainingTeamsManager(IRemainingTeamsState remainingTeamsState,
                                 ITeamsConfigState teamsConfigState, ITeamsManager teamsManager,
                                 MinecraftServer server) {
        this.remainingTeamsState = remainingTeamsState;
        this.teamsConfigState = teamsConfigState;
        this.teamsManager = teamsManager;
        this.server = server;
    }

    @Override
    public void removeTeam(TeamDto team) {
        remainingTeamsState.removeTeam(team.id());

        // Send event to clients
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(team.id());
        ServerEventSending.sendToEveryone(server, NetworkingConstants.TEAM_ELIMINATED, buf);
    }

    @Override
    public List<TeamDto> getRemainingTeams() {
        return remainingTeamsState.getRemainingTeams().stream().map(id -> teamsConfigState.getTeamOfId(id).orElseThrow()).toList();
    }

    @Override
    public boolean remains(TeamDto team) {
        return remainingTeamsState.remains(team.id());
    }

    @Override
    public void reset() {

        remainingTeamsState.reset(teamsConfigState.getTeams().stream()
                .filter(team -> !teamsManager.getPlayersOfTeam(team).isEmpty())
                .map(TeamDto::id)
                .toList());

        // Send event to clients
        ServerEventSending.sendToEveryone(server, NetworkingConstants.REMAINING_TEAMS_RESET, PacketByteBufs.empty());
    }
}
