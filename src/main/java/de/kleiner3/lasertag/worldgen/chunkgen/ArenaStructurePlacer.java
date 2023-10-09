package de.kleiner3.lasertag.worldgen.chunkgen;

import de.kleiner3.lasertag.mixin.IStructureTemplateAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.*;
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
     * @param arenaType
     * @param proceduralArenaType
     * @param chunk
     * @param world
     */
    public void placeArenaChunkSegment(ArenaType arenaType,
                                       ProceduralArenaType proceduralArenaType,
                                       Chunk chunk,
                                       StructureWorldAccess world,
                                       long seed) {

        // Get the current chunks block box
        var chunkBox = getBlockBoxForChunk(chunk);

        // Get the arena size
        var size = arenaType.getArenaSize(proceduralArenaType);

        // Get the startPos
        var startPos = BlockPos.ORIGIN.subtract(arenaType.placementOffset);

        // If chunk does not intersect with arena bounding box, do nothing
        if (chunkBox.getMaxX() < startPos.getX() || chunkBox.getMinX() > (startPos.getX() + size.getX()) ||
                chunkBox.getMaxZ() < startPos.getZ() || chunkBox.getMinZ() > (startPos.getZ() + size.getZ())) {
            return;
        }

        var arenaTemplate = arenaType.getArenaTemplate();

        this.placeArenaChunkSegment(arenaTemplate, chunkBox, startPos, world);
    }

    /**
     * Spawns all entites of an arena
     * @param type
     * @param chunkRegion
     */
    public void spawnEntitiesOfArena(ArenaType type, ChunkRegion chunkRegion) {

        var arenaTemplate = type.getArenaTemplate();
        var startPos = BlockPos.ORIGIN.subtract(type.placementOffset);

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

    public void reset() {
        // Nothing to reset
    }

    protected void placeArenaChunkSegment(StructureTemplate template,
                                          BlockBox chunkBox,
                                          Vec3i startPos,
                                          StructureWorldAccess world) {
        // Get block infos
        var blockInfos = ((IStructureTemplateAccessor)template).getBlockInfoLists().get(0).getAll();

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

                    // Post process the state
                    var postProcessState = Block.postProcessState(blockInfo.state, world, actualBlockPos);
                    if (postProcessState.isAir()) {
                        postProcessState = blockInfo.state;
                    }

                    // Place the block in the world
                    if (!world.setBlockState(actualBlockPos, postProcessState, Block.NOTIFY_LISTENERS)) {
                        return;
                    }

                    if (blockInfo.nbt != null) {
                        var blockEntity = world.getBlockEntity(actualBlockPos);

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
