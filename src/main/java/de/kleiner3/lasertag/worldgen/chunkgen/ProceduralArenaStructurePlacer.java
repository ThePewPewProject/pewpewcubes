package de.kleiner3.lasertag.worldgen.chunkgen;

import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

/**
 * Helper class to place all blocks of a procedural arena
 *
 * @author Étienne Muser
 */
public class ProceduralArenaStructurePlacer extends ArenaStructurePlacer{

    private static final int BLOCKS_PER_CHUNK = 16;

    // Start pos of the arena (lower left corner in block coordinates, shell not included)
    private BlockPos arenaStartPos = null;

    // The bounds of the arena in chunks (shell included)
    private Integer arenaMinChunkX = null;
    private Integer arenaMinChunkZ = null;
    private Integer arenaMaxChunkX = null;
    private Integer arenaMaxChunkZ = null;

    private Integer lobbyMinChunkX = null;
    private Integer lobbyMinChunkZ = null;
    private Integer lobbyMaxChunkX = null;
    private Integer lobbyMaxChunkZ = null;

    private Integer completeChunkXSize = null;
    private Integer completeChunkZSize = null;

    private Vec3i completeOffset = null;

    private List<StructureTemplate> randomTemplates = null;

    @Override
    public void placeArenaChunkSegment(ArenaType arenaType, ProceduralArenaType proceduralArenaType, Chunk chunk, StructureWorldAccess world, long seed) {

        var chunkBox = getBlockBoxForChunk(chunk);

        var lobbyStartPos = BlockPos.ORIGIN.subtract(proceduralArenaType.lobbyOffset);

        // Get the chunk coords
        var chunkX = chunk.getPos().x;
        var chunkZ = chunk.getPos().z;

        if (lobbyMinChunkX == null) {
            initLobbySize(proceduralArenaType, lobbyStartPos);
        }

        // If is lobby chunk
        if (this.isInside(chunkX,
                chunkZ,
                lobbyMinChunkX,
                lobbyMinChunkZ,
                lobbyMaxChunkX,
                lobbyMaxChunkZ)) {
            // Place the lobby into the world
            this.placeLobbyChunkSegment(proceduralArenaType, chunkBox, lobbyStartPos, world);
            return;
        }

        if (arenaMinChunkX == null) {
            initArenaSize(proceduralArenaType);
        }

        // If is not arena chunk
        if (!this.isInside(chunkX,
                chunkZ,
                arenaMinChunkX,
                arenaMinChunkZ,
                arenaMaxChunkX,
                arenaMaxChunkZ)) {
            return;
        }

        // Place the arena into the world
        this.placeArenaChunkSegment(proceduralArenaType, chunkX, chunkZ, chunkBox, world, seed);
    }

    @Override
    public void spawnEntitiesOfArena(ArenaType type, ChunkRegion chunkRegion) {
        // No entities to spawn
    }

    @Override
    public void reset() {
        randomTemplates = null;
    }

    public Vec3i getCompleteSize(ProceduralArenaType proceduralArenaType) {

        if (completeChunkXSize == null) {
            initArenaSize(proceduralArenaType);
        }

        return new Vec3i(completeChunkXSize * BLOCKS_PER_CHUNK,
                Math.max(proceduralArenaType.getLobbyTemplate().getSize().getY(), proceduralArenaType.getShellTemplate().getSize().getY()),
                completeChunkZSize * BLOCKS_PER_CHUNK);
    }

    public Vec3i getCompleteOffset(ProceduralArenaType proceduralArenaType) {

        if (completeOffset == null) {
            initArenaSize(proceduralArenaType);
        }

        return completeOffset;
    }

    public StructureTemplate getRandomTemplate(ProceduralArenaType proceduralArenaType, int chunkX, int chunkZ, long seed) {

        if (randomTemplates == null) {
            randomTemplates = proceduralArenaType.getRandomTemplates(seed);
        }

        return randomTemplates.get(this.getSegmentId(proceduralArenaType, chunkX, chunkZ));
    }

    private void placeLobbyChunkSegment(ProceduralArenaType proceduralArenaType, BlockBox chunkBox, Vec3i startPos, StructureWorldAccess world) {

        var lobbyTemplate = proceduralArenaType.getLobbyTemplate();

        this.placeArenaChunkSegment(lobbyTemplate, chunkBox, startPos, world);

    }

    private void placeArenaChunkSegment(ProceduralArenaType proceduralArenaType,
                                        int chunkX,
                                        int chunkZ,
                                        BlockBox chunkBox,
                                        StructureWorldAccess world,
                                        long seed) {

        // Build shell
        var shellTemplate = proceduralArenaType.getShellTemplate();
        this.placeArenaChunkSegment(shellTemplate, chunkBox, arenaStartPos.add(new Vec3i(-1, -1, -1)), world);

        // Check if is arena or just shell (+/- 1 offset because of shell)
        if (!this.isInside(chunkX,
                chunkZ,
                arenaMinChunkX + 1,
                arenaMinChunkZ + 1,
                arenaMaxChunkX - 1,
                arenaMaxChunkZ - 1)) {
            return;
        }

        var arenaChunkX = chunkX - this.arenaMinChunkX - 1;
        var arenaChunkZ = chunkZ - this.arenaMinChunkZ - 1;
        var arenaSegmentTemplate = this.getRandomTemplate(proceduralArenaType, arenaChunkX, arenaChunkZ, seed);
        var localOffset = this.getLocalChunkOffset(proceduralArenaType, this.getSegmentId(proceduralArenaType, arenaChunkX, arenaChunkZ));
        this.placeArenaChunkSegment(arenaSegmentTemplate, chunkBox, arenaStartPos.add(localOffset), world);
    }

    private void initLobbySize(ProceduralArenaType proceduralArenaType, BlockPos lobbyStartPos) {
        var lobbyTemplate = proceduralArenaType.getLobbyTemplate();
        var lobbySize = lobbyTemplate.getSize();
        // Integer division intended
        var lobbyEndX = lobbyStartPos.getX() + lobbySize.getX();
        var lobbyEndZ = lobbyStartPos.getZ() + lobbySize.getZ();
        lobbyMinChunkX = ChunkSectionPos.getSectionCoord(lobbyStartPos.getX());
        lobbyMinChunkZ = ChunkSectionPos.getSectionCoord(lobbyStartPos.getZ());
        lobbyMaxChunkX = ChunkSectionPos.getSectionCoord(lobbyEndX);
        lobbyMaxChunkZ = ChunkSectionPos.getSectionCoord(lobbyEndZ);
    }

    private void initArenaSize(ProceduralArenaType proceduralArenaType) {

        if (lobbyMinChunkX == null) {
            initLobbySize(proceduralArenaType, BlockPos.ORIGIN.subtract(proceduralArenaType.lobbyOffset));
        }

        var segments = proceduralArenaType.segments;

        int startChunkX;
        int startChunkZ;
        int arenaChunkSizeX = proceduralArenaType.segments.length;
        int arenaChunkSizeZ = proceduralArenaType.segments[0].length;
        if (lobbyMaxChunkX > lobbyMaxChunkZ) {
            startChunkZ = lobbyMaxChunkZ + 1;
            // Integer division intended
            startChunkX = -(segments.length / 2) - 1;

            completeChunkXSize = Math.max(arenaChunkSizeX + 2, lobbyMaxChunkX - lobbyMinChunkX);
            completeChunkZSize = (lobbyMaxChunkZ - lobbyMinChunkZ) + arenaChunkSizeZ + 2;
        } else {
            startChunkX = lobbyMaxChunkX + 1;
            // Integer division intended
            startChunkZ = -(segments[0].length / 2) - 1;

            completeChunkXSize = (lobbyMaxChunkX - lobbyMinChunkX) + arenaChunkSizeX + 2;
            completeChunkZSize = Math.max(arenaChunkSizeZ + 2, lobbyMaxChunkZ - lobbyMinChunkZ);
        }

        this.arenaStartPos = new BlockPos((startChunkX + 1) * BLOCKS_PER_CHUNK, 0, (startChunkZ + 1) * BLOCKS_PER_CHUNK);
        this.arenaMinChunkX = startChunkX;
        this.arenaMinChunkZ = startChunkZ;
        // Plus one for the shell
        this.arenaMaxChunkX = this.arenaMinChunkX + arenaChunkSizeX + 1;
        this.arenaMaxChunkZ = this.arenaMinChunkZ + arenaChunkSizeZ + 1;

        var completeMinChunkX = Math.min(lobbyMinChunkX, arenaMinChunkX);
        var completeMinChunkZ = Math.min(lobbyMinChunkZ, arenaMinChunkZ);
        this.completeOffset = new Vec3i(-completeMinChunkX * BLOCKS_PER_CHUNK, 0, -completeMinChunkZ * BLOCKS_PER_CHUNK);
    }

    private boolean isInside(int x, int z, int minX, int minZ, int maxX, int maxZ) {
        var xInside = x >= minX && x <= maxX;
        var zInside = z >= minZ && z <= maxZ;

        return xInside && zInside;
    }

    private int getSegmentId(ProceduralArenaType proceduralArenaType, int chunkX, int chunkZ) {
        return proceduralArenaType.segments[chunkX][chunkZ];
    }

    /**
     * Calculates the offset of the first chunk of the given segmentId inside the arena.
     *
     * @param proceduralArenaType
     * @param segmentId
     * @return The offset in blocks
     */
    private BlockPos getLocalChunkOffset(ProceduralArenaType proceduralArenaType, int segmentId) {
        int localChunkStartX = 0;
        int localChunkStartZ;
        for (var row : proceduralArenaType.segments) {
            localChunkStartZ = 0;

            for (var chunk : row) {
                if (chunk == segmentId) {
                    return new BlockPos(localChunkStartX * BLOCKS_PER_CHUNK, 0, localChunkStartZ * BLOCKS_PER_CHUNK);
                }

                ++localChunkStartZ;
            }

            ++localChunkStartX;
        }

        return null;
    }
}
