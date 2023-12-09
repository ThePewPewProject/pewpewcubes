package de.kleiner3.lasertag.worldgen.chunkgen.placer;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ProceduralArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.template.ArenaTemplate;
import de.kleiner3.lasertag.worldgen.chunkgen.template.ProceduralArenaTemplate;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

/**
 * Helper class to place all blocks of a procedural arena
 *
 * @author Étienne Muser
 */
public class ProceduralArenaStructurePlacer extends ArenaStructurePlacer {

    private static final int BLOCKS_PER_CHUNK = 16;

    @Override
    public void placeArenaChunkSegment(ArenaTemplate template,
                                       Chunk chunk,
                                       StructureWorldAccess world) {

        // Sanity check
        if (template == null) {
            LasertagMod.LOGGER.warn("Arena generation: Skipping chunk '" + chunk + "'");
            return;
        }

        // Try to cast
        if (!(template instanceof ProceduralArenaTemplate proceduralArenaTemplateArenaTemplate)) {
            throw new UnsupportedOperationException("Unsupported arena template type '" + template.getClass().getName() + "'");
        }

        var chunkBox = getBlockBoxForChunk(chunk);


        // Get the chunk coords
        var chunkX = chunk.getPos().x;
        var chunkZ = chunk.getPos().z;

        // If is lobby chunk
        if (proceduralArenaTemplateArenaTemplate.isLobbyChunk(chunkX, chunkZ)) {

            // Place the lobby into the world
            this.placeLobbyChunkSegment(proceduralArenaTemplateArenaTemplate, chunkBox, world);
            return;
        }

        // If is not arena chunk
        if (!proceduralArenaTemplateArenaTemplate.isArenaChunk(chunkX, chunkZ)) {
            return;
        }

        // Place the arena into the world
        this.placeArenaChunkSegment(proceduralArenaTemplateArenaTemplate, chunkX, chunkZ, chunkBox, world);
    }

    @Override
    public void spawnEntitiesOfArena(ArenaTemplate template, ChunkRegion chunkRegion) {
        // No entities to spawn
    }

    private void placeLobbyChunkSegment(ProceduralArenaTemplate proceduralArenaTemplate, BlockBox chunkBox, StructureWorldAccess world) {

        var lobbyTemplate = proceduralArenaTemplate.getLobbyTemplate();

        this.placeArenaChunkSegment(lobbyTemplate, chunkBox, world);

    }

    private void placeArenaChunkSegment(ProceduralArenaTemplate proceduralArenaTemplate,
                                        int chunkX,
                                        int chunkZ,
                                        BlockBox chunkBox,
                                        StructureWorldAccess world) {

        var proceduralArenaType = proceduralArenaTemplate.getProceduralArenaType();

        // Build shell
        var shellTemplate = proceduralArenaTemplate.getShellTemplate();
        this.placeArenaChunkSegment(shellTemplate, chunkBox, world);

        // Check if is arena or just shell
        if (!proceduralArenaTemplate.isArenaChunkWithoutShell(chunkX, chunkZ)) {
            return;
        }

        var arenaChunkX = chunkX - proceduralArenaTemplate.getArenaMinChunkX() - 1;
        var arenaChunkZ = chunkZ - proceduralArenaTemplate.getArenaMinChunkZ() - 1;
        var segmentId = this.getSegmentId(proceduralArenaType, arenaChunkX, arenaChunkZ);
        var arenaSegmentTemplate = proceduralArenaTemplate.getRandomTemplate(segmentId);
        this.placeArenaChunkSegment(arenaSegmentTemplate, chunkBox, world);
    }

    private int getSegmentId(ProceduralArenaType proceduralArenaType, int chunkX, int chunkZ) {
        return proceduralArenaType.segments[chunkX][chunkZ];
    }
}
