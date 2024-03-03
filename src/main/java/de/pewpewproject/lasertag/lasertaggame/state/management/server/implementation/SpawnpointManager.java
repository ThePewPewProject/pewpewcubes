package de.pewpewproject.lasertag.lasertaggame.state.management.server.implementation;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.ISpawnpointManager;
import de.pewpewproject.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.TeamsConfigState;
import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;
import de.pewpewproject.lasertag.networking.NetworkingConstants;
import de.pewpewproject.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of ISpawnpointManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class SpawnpointManager implements ISpawnpointManager {

    private final HashMap<TeamDto, ArrayList<BlockPos>> spawnpointCache = new HashMap<>();

    private final ITeamsConfigState teamsConfigState;

    public SpawnpointManager(ITeamsConfigState teamsConfigState) {
        this.teamsConfigState = teamsConfigState;
    }

    /**
     * Gets all spawnpoints for the given team from the cache
     *
     * @param team
     * @return
     */
    @Override
    public ArrayList<BlockPos> getSpawnpoints(TeamDto team) {
        return spawnpointCache.get(team);
    }

    /**
     * Gets all spawnpoints for all teams in a unified list
     *
     * @return
     */
    @Override
    public List<BlockPos> getAllSpawnpoints() {

        return spawnpointCache.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }

    @Override
    public void initSpawnpointCacheIfNecessary(ServerWorld world, boolean scanSpawnpoints) {

        if (spawnpointCache.isEmpty() || scanSpawnpoints) {

            initSpawnpointCache(world);
        }
    }

    @Override
    public void clearSpawnpointCache() {
        this.spawnpointCache.clear();
    }

    /**
     * Initializes the spawnpoint cache. Searches a 31 x 31 chunk area for spawnpoint blocks specified by the team.
     * This method is computationally intensive, don't call too often or when responsiveness is important. The call of this method blocks the server from ticking!
     */
    private void initSpawnpointCache(ServerWorld world) {

        try {
            this.spawnpointCache.clear();

            // Initialize team lists
            for (var team : teamsConfigState.getTeams()) {

                // Skip spectators
                if (team.equals(TeamsConfigState.SPECTATORS)) {
                    continue;
                }

                spawnpointCache.put(team, new ArrayList<>());
            }

            // Start time measurement
            var startTime = System.nanoTime();

            // Iterate over blocks and find spawnpoints
            world.fastSearchBlock((block, pos) -> {
                for (var teamDto : teamsConfigState.getTeams()) {

                    // Skip spectators
                    if (teamDto.equals(TeamsConfigState.SPECTATORS)) {
                        continue;
                    }

                    if (teamDto.spawnpointBlock().equals(block)) {
                        var team = spawnpointCache.get(teamDto);
                        synchronized (this) {
                            team.add(pos);
                        }
                        break;
                    }
                }
            }, (currChunk, maxChunk) -> {
                // Only send a progress update every second chunk to not ddos our players
                if (currChunk % 2 == 0) {
                    return;
                }

                // Create packet buffer
                var buf = new PacketByteBuf(Unpooled.buffer());

                // Write progress to buffer
                buf.writeDouble((double) currChunk / (double) maxChunk);

                ServerEventSending.sendToEveryone(world.getServer(), NetworkingConstants.PROGRESS, buf);
            });

            // Stop time measurement
            var stopTime = System.nanoTime();
            var duration = (stopTime - startTime) / 1000000000.0;
            LasertagMod.LOGGER.info("Spawnpoint search took " + duration + "s.");
        } catch (Exception ex) {

            LasertagMod.LOGGER.error("Unexpected error while scanning for spawnpoints: ", ex);

        } finally {

            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            // Write progress to buffer
            buf.writeDouble(-1.0F);

            ServerEventSending.sendToEveryone(world.getServer(), NetworkingConstants.PROGRESS, buf);
        }
    }
}
