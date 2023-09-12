package de.kleiner3.lasertag.worldgen.chunkgen;

import de.kleiner3.lasertag.mixin.IStructureTemplateAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
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

                    var x = startPos.getX() + pos.x;
                    var y = startPos.getY() + pos.y;
                    var z = startPos.getZ() + pos.z;

                    // Get the chunk coords
                    var chunkX = ChunkSectionPos.getSectionCoord(x);
                    var chunkZ = ChunkSectionPos.getSectionCoord(z);

                    // If chunk of entity is not loaded
                    if (!chunkRegion.isChunkLoaded(chunkX, chunkZ)) {
                        // Do nothing
                        return;
                    }

                    var isAlreadyAdapted = entityInfo.nbt.contains("lasertag:tileAdapted");

                    if (!isAlreadyAdapted) {

                        // Overwrite position
                        var posList = new NbtList();
                        posList.add(0, NbtDouble.of(x));
                        posList.add(1, NbtDouble.of(y));
                        posList.add(2, NbtDouble.of(z));
                        entityInfo.nbt.put("Pos", posList);

                        var hasTileXYZ = entityInfo.nbt.contains("TileX");
                        if (hasTileXYZ) {
                            // Overwrite tile position
                            var tileX = startPos.getX() + entityInfo.nbt.getInt("TileX");
                            var tileY = startPos.getY() + entityInfo.nbt.getInt("TileY");
                            var tileZ = startPos.getZ() + entityInfo.nbt.getInt("TileZ");
                            entityInfo.nbt.putInt("TileX", tileX);
                            entityInfo.nbt.putInt("TileY", tileY);
                            entityInfo.nbt.putInt("TileZ", tileZ);
                        }

                        entityInfo.nbt.putBoolean("lasertag:tileAdapted", true);
                    }

                    // Create the entity
                    var entityOptional = EntityType.getEntityFromNbt(entityInfo.nbt, chunkRegion.getServer().getOverworld());

                    // Spawn the entity if it is present
                    entityOptional.ifPresent(chunkRegion.getServer().getOverworld()::spawnEntity);
                });
    }
}
