package de.kleiner3.lasertag.worldgen.chunkgen.placer;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.mixin.IStructureTemplateAccessor;
import de.kleiner3.lasertag.worldgen.chunkgen.template.ArenaTemplate;
import de.kleiner3.lasertag.worldgen.chunkgen.template.PrebuildArenaTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

/**
 * Helper class to place all blocks of an arena
 *
 * @author Étienne Muser
 */
public class ArenaStructurePlacer {

    /**
     * Places all blocks of an arena in the specified chunk
     * @param template
     * @param chunk
     * @param world
     */
    public void placeArenaChunkSegment(ArenaTemplate template,
                                       Chunk chunk,
                                       StructureWorldAccess world) {

        // Sanity check
        if (template == null) {
            LasertagMod.LOGGER.warn("Arena generation: Skipping chunk '" + chunk + "'");
            return;
        }

        // Try to cast
        if (!(template instanceof PrebuildArenaTemplate prebuildArenaTemplate)) {
            throw new UnsupportedOperationException("Unsupported arena template type '" + template.getClass().getName() + "'");
        }

        // Get the current chunks block box
        var chunkBox = getBlockBoxForChunk(chunk);

        // Get the arena size
        var size = prebuildArenaTemplate.getArenaSize();

        // Get the startPos
        var startPos = prebuildArenaTemplate.getStartPos();

        // If chunk does not intersect with arena bounding box, do nothing
        if (chunkBox.getMaxX() < startPos.getX() || chunkBox.getMinX() > (startPos.getX() + size.getX()) ||
                chunkBox.getMaxZ() < startPos.getZ() || chunkBox.getMinZ() > (startPos.getZ() + size.getZ())) {
            return;
        }

        var structureTemplate = prebuildArenaTemplate.getArenaTemplate();

        this.placeArenaChunkSegment(structureTemplate, chunkBox, world);
    }

    /**
     * Spawns all entites of an arena
     * @param template
     * @param chunkRegion
     */
    public void spawnEntitiesOfArena(ArenaTemplate template, ChunkRegion chunkRegion) {

        // Sanity check
        if (template == null) {
            LasertagMod.LOGGER.warn("Arena generation: Skipping chunk region '" + chunkRegion + "'");
            return;
        }

        // Try to cast
        if (!(template instanceof PrebuildArenaTemplate prebuildArenaTemplate)) {
            throw new UnsupportedOperationException("Unsupported arena template type '" + template.getClass().getName() + "'");
        }

        var arenaTemplate = prebuildArenaTemplate.getArenaTemplate();

        // Get the entity infos
        var entityInfos = ((IStructureTemplateAccessor)arenaTemplate).getEntities();

        // For every entity
        entityInfos.forEach((entityInfo) -> {

                    // Get the chunk coords
                    var chunkX = ChunkSectionPos.getSectionCoord(entityInfo.pos.x);
                    var chunkZ = ChunkSectionPos.getSectionCoord(entityInfo.pos.z);

                    // If chunk of entity is not loaded
                    if (!chunkRegion.isChunkLoaded(chunkX, chunkZ)) {
                        // Do nothing
                        return;
                    }

                    // Create the entity
                    var entityOptional = EntityType.getEntityFromNbt(entityInfo.nbt, chunkRegion.getServer().getOverworld());

                    // Spawn the entity if it is present
                    entityOptional.ifPresent(chunkRegion.getServer().getOverworld()::spawnEntity);
                });
    }

    protected void placeArenaChunkSegment(StructureTemplate template,
                                          BlockBox chunkBox,
                                          StructureWorldAccess world) {
        // Get block infos
        var blockInfos = ((IStructureTemplateAccessor)template).getBlockInfoLists().get(0).getAll();

        // For every block in the chunkbox
        blockInfos.forEach((blockInfo) -> {

                    // If the block is not inside the chunk
                    if (!chunkBox.contains(blockInfo.pos)) {
                        return;
                    }

                    // Get the block state
                    var blockState = blockInfo.state;

                    // Optimization: If the block is air, then do nothing
                    if (blockState.equals(Blocks.AIR.getDefaultState())) {
                        return;
                    }

                    // Place the block in the world
                    if (!world.setBlockState(blockInfo.pos, blockState, Block.NOTIFY_LISTENERS)) {
                        return;
                    }

                    if (blockInfo.nbt != null) {
                        var blockEntity = world.getBlockEntity(blockInfo.pos);

                        if (blockEntity != null) {
                            blockEntity.readNbt(blockInfo.nbt);
                        }
                    }
                });
    }

    protected static BlockBox getBlockBoxForChunk(Chunk chunk) {

        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        HeightLimitView heightLimitView = chunk.getHeightLimitView();
        int startY = heightLimitView.getBottomY() + 1;
        int endY = heightLimitView.getTopY() - 1;
        return new BlockBox(startX, startY, startZ, startX + 15, endY, startZ + 15);
    }
}
