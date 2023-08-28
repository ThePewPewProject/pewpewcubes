package de.kleiner3.lasertag.worldgen.chunkgen;

import de.kleiner3.lasertag.mixin.IStructureTemplateAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;

/**
 * Helper class to place all blocks of a arena
 *
 * @author Étienne Muser
 */
public class ArenaStructurePlacer {

    /**
     * Places all blocks of an arena in the specified chunk
     * @param arenaTemplate
     * @param chunkBox
     * @param startPos
     * @param world
     */
    public static void placeArenaChunkSegment(StructureTemplate arenaTemplate,
                                              BlockBox chunkBox,
                                              Vec3i startPos,
                                              StructureWorldAccess world) {
        // Get block infos
        var blockInfos = ((IStructureTemplateAccessor)arenaTemplate).getBlockInfoLists().get(0).getAll();

        // For every block in the chunkbox
        blockInfos.stream()
                .filter((blockInfo) -> chunkBox.contains(startPos.add(blockInfo.pos)))
                .forEach((blockInfo) -> {
                    // Get the actual block pos
                    var actualBlockPos = new BlockPos(startPos.add(blockInfo.pos));

                    // Get the block state
                    var blockState = blockInfo.state;

                    // Optimization: If the block is air, then do nothing
                    if (blockState.equals(Blocks.AIR.getDefaultState())) {
                        return;
                    }

                    // Place the block in the world
                    world.setBlockState(actualBlockPos, blockState, 2);

                    if (blockInfo.nbt != null) {
                        var blockEntity = world.getBlockEntity(actualBlockPos);

                        blockEntity.readNbt(blockInfo.nbt);
                    }
                });
    }

    /**
     * Spawns all entites of an arena
     * @param arenaTemplate
     * @param startPos
     * @param chunkRegion
     */
    public static void spawnEntitiesOfArena(StructureTemplate arenaTemplate,
                                            Vec3i startPos,
                                            ChunkRegion chunkRegion) {
        // Get the entity infos
        var entityInfos = ((IStructureTemplateAccessor)arenaTemplate).getEntities();

        // For every entity
        entityInfos.forEach((entityInfo) -> {
                    // Get the position
                    var pos = entityInfo.pos;

                    // Get the chunk coords
                    var chunkX = ChunkSectionPos.getSectionCoord(startPos.getX() + pos.x);
                    var chunkZ = ChunkSectionPos.getSectionCoord(startPos.getZ() + pos.z);

                    // If chunk of entity is not loaded
                    if (!chunkRegion.isChunkLoaded(chunkX, chunkZ)) {
                        // Do nothing
                        return;
                    }

                    // Create the entity
                    var entityOptional = EntityType.getEntityFromNbt(entityInfo.nbt, chunkRegion.getServer().getOverworld());

                    // If entity not empty
                    entityOptional.ifPresent((entity) -> {
                        // Set the position
                        entity.setPos(startPos.getX() + pos.x,
                                      startPos.getY() + pos.y,
                                      startPos.getZ() + pos.z);

                        // Spawn the entity
                        chunkRegion.spawnEntity(entity);
                    });
                });
    }
}
