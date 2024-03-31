package de.pewpewproject.lasertag.worldgen.chunkgen.placer;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.mixin.IStructureTemplateAccessor;
import de.pewpewproject.lasertag.worldgen.chunkgen.template.ArenaTemplate;
import de.pewpewproject.lasertag.worldgen.chunkgen.template.PrebuildArenaTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Helper class to place all blocks of an arena
 *
 * @author Étienne Muser
 */
public class ArenaStructurePlacer {

    /**
     * Places all blocks of an arena in the specified chunk
     *
     * @param template
     * @param chunk
     * @param world
     */
    public void placeArenaChunkSegment(ArenaTemplate template,
                                       Chunk chunk,
                                       StructureWorldAccess world,
                                       boolean isGenerate) {

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

        this.placeArenaChunkSegment(structureTemplate, chunkBox, world, chunk, isGenerate);
    }

    /**
     * Spawns all entites of an arena
     *
     * @param template
     * @param chunkRegion
     */
    public void spawnEntitiesOfArena(ArenaTemplate template, StructureWorldAccess chunkRegion) {

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
        var entityInfos = ((IStructureTemplateAccessor) arenaTemplate).getEntities();

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
                                          StructureWorldAccess world,
                                          Chunk chunk,
                                          boolean isGenerate) {
        // Get block infos
        var blockInfos = ((IStructureTemplateAccessor) template).getBlockInfoLists().get(0).getAll();

        // For every block in the chunkbox
        blockInfos.stream()
                .filter(blockInfo -> chunkBox.contains(blockInfo.pos))
                .forEach(blockInfo -> {

                    if (isGenerate) {
                        setBlockGenerate(world, blockInfo);
                    } else {
                        setBlockLoad((WorldChunk) chunk, (ServerWorld) world, blockInfo);
                    }
                });

        if (!isGenerate) {
            chunk.setNeedsSaving(true);
        }
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

    private void setBlockGenerate(StructureWorldAccess world, StructureTemplate.StructureBlockInfo blockInfo) {

        // Place the block in the world
        if (!world.setBlockState(blockInfo.pos, blockInfo.state, Block.NOTIFY_LISTENERS)) {
            return;
        }

        if (blockInfo.nbt != null) {
            var blockEntity = world.getBlockEntity(blockInfo.pos);

            if (blockEntity != null) {
                blockEntity.readNbt(blockInfo.nbt);
            }
        }
    }

    private void setBlockLoad(WorldChunk chunk, ServerWorld world, StructureTemplate.StructureBlockInfo blockInfo) {
        var x = blockInfo.pos.getX();
        var y = blockInfo.pos.getY();
        var z = blockInfo.pos.getZ();

        // TODO: Save world not working

        // Place the block in the world
        world.fastSetBlock(chunk, blockInfo.pos, blockInfo.state, Block.FORCE_STATE);
        //chunk.setBlockState(blockInfo.pos, blockInfo.state, false);
        //chunk.getSection(chunk.getSectionIndex(y)).setBlockState(x & 15, y & 15, z & 15, blockInfo.state, false);

        // TODO: Block entities

        if (blockInfo.nbt != null) {
            var blockEntity = world.getBlockEntity(blockInfo.pos);
            if (blockEntity != null) {
                blockEntity.readNbt(blockInfo.nbt);
            }
        }
    }
}
