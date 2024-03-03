package de.pewpewproject.lasertag.lasertaggame.state.management.server;

import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Interface for a server spawnpoint manager
 *
 * @author Étienne Muser
 */
public interface ISpawnpointManager {

    /**
     * Get the list of all spawnpoints of a team
     *
     * @param team The team to get the spawnpoints of
     * @return List containing all spawnpoints as a BlockPos
     */
    List<BlockPos> getSpawnpoints(TeamDto team);

    /**
     * Get the list of all spawnpoints of all teams
     *
     * @return List containing the spawnpoints of all teams as a BlockPos
     */
    List<BlockPos> getAllSpawnpoints();

    /**
     * Initialize the spawnpoint cache. Skip if this was executed already. Scan anyway if the scanSpawnpoints flag is set.
     *
     * @param world           The world to scan
     * @param scanSpawnpoints Flag to indicate the spawnpoints should be scanned even if this was executed already
     */
    void initSpawnpointCacheIfNecessary(ServerWorld world, boolean scanSpawnpoints);

    /**
     * Clear the spawnpoints
     */
    void clearSpawnpointCache();
}
