package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IEliminationManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.IEliminationState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.UIState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the IElimination manager for the lasertag game
 *
 * @author Étienne Muser
 */
public class EliminationManager implements IEliminationManager {

    /**
     * The number of lasertag ticks since the last phase change.
     * Initialize with -1 as there will be a tick at 0 seconds
     * into the game.
     */
    private long ticksSinceLastPhaseChange = -1L;

    /**
     * Map mapping every team to their survive time in seconds
     *     key: The team
     *     value: The teams survive time in seconds
     */
    private final Map<TeamDto, Long> teamSurviveTimeMap = new ConcurrentHashMap<>();

    /**
     * Map mapping every players uuid to their survive time in seconds
     *     key: The players uuid
     *     value: The players survive time in seconds
     */
    private final Map<UUID, Long> playerSurviveTimeMap = new ConcurrentHashMap<>();

    private final MinecraftServer server;

    private final IEliminationState eliminationState;

    private final ISettingsManager settingsManager;

    private final UIState uiState;

    public EliminationManager(MinecraftServer server, IEliminationState eliminationState, ISettingsManager settingsManager, UIState uiState) {
        this.server = server;
        this.eliminationState = eliminationState;
        this.settingsManager = settingsManager;
        this.uiState = uiState;
    }

    @Override
    public synchronized void eliminatePlayer(UUID eliminatedPlayerUuid, UUID shooterUuid) {

        // Eliminate the player
        eliminationState.eliminatePlayer(eliminatedPlayerUuid);

        // Put the players survive time
        playerSurviveTimeMap.put(eliminatedPlayerUuid, uiState.gameTime);

        var newEliminationCount = 0L;

        // If there is a shooter
        if (shooterUuid != null) {

            // Get the shooters elimination count
            var oldEliminationCount = eliminationState.getEliminationCount(shooterUuid);

            // Increase the elimination count
            newEliminationCount = oldEliminationCount + 1;
            eliminationState.setEliminationCount(shooterUuid, newEliminationCount);
        }

        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeUuid(eliminatedPlayerUuid);
        buf.writeUuid(shooterUuid);
        buf.writeLong(newEliminationCount);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.PLAYER_ELIMINATED, buf);
    }

    @Override
    public boolean isPlayerEliminated(UUID playerUuid) {
        return eliminationState.isEliminated(playerUuid);
    }

    @Override
    public long getPlayerEliminationCount(UUID playerUuid) {
        return eliminationState.getEliminationCount(playerUuid);
    }

    @Override
    public void reset() {
        eliminationState.reset();
        teamSurviveTimeMap.clear();
        playerSurviveTimeMap.clear();

        ServerEventSending.sendToEveryone(server, NetworkingConstants.ELIMINATION_STATE_RESET, PacketByteBufs.empty());
    }

    @Override
    public void tick() {

        // Increment time since last phase change
        ++ticksSinceLastPhaseChange;

        // If the phase is now over
        if (ticksSinceLastPhaseChange >= settingsManager.<Long>get(SettingDescription.PHASE_DURATION)) {

            // Reset the time since last phase change
            ticksSinceLastPhaseChange = 0L;

            // Shrink the border
            shrinkBorder();
        }
    }

    @Override
    public void setTeamSurviveTime(TeamDto team, long surviveTime) {
        teamSurviveTimeMap.put(team, surviveTime);
    }

    @Override
    public Long getTeamSurviveTime(TeamDto team) {
        return teamSurviveTimeMap.get(team);
    }

    @Override
    public Long getPlayerSurviveTime(UUID playerUuid) {
        return playerSurviveTimeMap.get(playerUuid);
    }

    private void shrinkBorder() {

        // Get the world border
        var worldBorder = server.getOverworld().getWorldBorder();

        // Get the shrink distance
        var shrinkDistance = settingsManager.<Long>get(SettingDescription.BORDER_SHRINK_DISTANCE) * 2;

        // Get the shrink time
        var shrinkTime = settingsManager.<Long>get(SettingDescription.BORDER_SHRINK_TIME);

        // Get the current border size
        var currentBorderSize = worldBorder.getSize();

        // Calculate the new border size
        var newBorderSize = Math.max(0, currentBorderSize - shrinkDistance);

        // If the border should not shrink
        if (newBorderSize == currentBorderSize) {
            return;
        }

        // Shrink the border
        if (shrinkTime > 0L) {

            // Calculate totalShrinkTime
            var totalShrinkTime = shrinkTime * 1000L;

            worldBorder.interpolateSize(currentBorderSize, newBorderSize, totalShrinkTime);
        } else {

            worldBorder.setSize(newBorderSize);
        }
    }
}
