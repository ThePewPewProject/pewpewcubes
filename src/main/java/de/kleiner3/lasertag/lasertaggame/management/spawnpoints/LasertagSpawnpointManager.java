package de.kleiner3.lasertag.lasertaggame.management.spawnpoints;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Class to manage the lasertag spawnpoints
 *
 * @author Étienne Muser
 */
public class LasertagSpawnpointManager implements IManager {

    private final HashMap<TeamDto, ArrayList<BlockPos>> spawnpointCache = new HashMap<>();


    /**
     * Gets all spawnpoints for the given team from the cache
     *
     * @param team
     * @return
     */
    public ArrayList<BlockPos> getSpawnpoints(TeamDto team) {
        return spawnpointCache.get(team);
    }

    /**
     * Gets all spawnpoints for all teams in a unified list
     *
     * @return
     */
    public List<BlockPos> getAllSpawnpoints() {

        return spawnpointCache.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }

    public void initSpawnpointCacheIfNecessary(ServerWorld world, boolean scanSpawnpoints) {

        if (spawnpointCache.isEmpty() || scanSpawnpoints) {

            initSpawnpointCache(world);
        }
    }

    public void clearSpawnpointCache() {
        this.spawnpointCache.clear();
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    /**
     * Initializes the spawnpoint cache. Searches a 31 x 31 chunk area for spawnpoint blocks specified by the team.
     * This method is computationally intensive, don't call too often or when responsiveness is important. The call of this method blocks the server from ticking!
     */
    private void initSpawnpointCache(ServerWorld world) {

        try {
            this.spawnpointCache.clear();

            var teamConfig = LasertagGameManager.getInstance().getTeamManager().getTeamConfigManager().teamConfig;

            // Initialize team lists
            for (var team : teamConfig.values()) {

                // Skip spectators
                if (team.equals(TeamConfigManager.SPECTATORS)) {
                    continue;
                }

                spawnpointCache.put(team, new ArrayList<>());
            }

            // Start time measurement
            var startTime = System.nanoTime();

            // Iterate over blocks and find spawnpoints
            world.fastSearchBlock((block, pos) -> {
                for (var teamDto : teamConfig.values()) {

                    // Skip spectators
                    if (teamDto.equals(TeamConfigManager.SPECTATORS)) {
                        continue;
                    }

                    if (teamDto.spawnpointBlock().equals(block)) {
                        var team = spawnpointCache.get(teamDto);
                        synchronized (teamDto) {
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

                ServerEventSending.sendToEveryone(world, NetworkingConstants.PROGRESS, buf);
            });

            // Stop time measurement
            var stopTime = System.nanoTime();
            var duration = (stopTime - startTime) / 1000000000.0;
            LasertagMod.LOGGER.info("Spawnpoint search took " + duration + "s.");
        } catch (Exception ex) {

            LasertagMod.LOGGER.error("Unexpected error while scanning for spawnpoints: ", ex);

            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            // Write progress to buffer
            buf.writeDouble(1.0F);

            ServerEventSending.sendToEveryone(world, NetworkingConstants.PROGRESS, buf);
        }
    }
}
