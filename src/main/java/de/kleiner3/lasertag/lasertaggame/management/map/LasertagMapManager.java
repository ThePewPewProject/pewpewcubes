package de.kleiner3.lasertag.lasertaggame.management.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Unit;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaChunkGeneratorConfig;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.ProceduralArenaType;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionOptions;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class to manage the lasertag maps.
 *
 * @author Étienne Muser
 */
public class LasertagMapManager implements IManager {

    private final MinecraftServer server;

    public LasertagMapManager(MinecraftServer server) {
        this.server = server;
    }

    //region Public methods

    public void loadMap(ArenaType newArenaType, ProceduralArenaType newProceduralArenaType) {

        // Step 0: Check if this is an arena world
        var chunkGenerator = Objects.requireNonNull(server.getSaveProperties().getGeneratorOptions().getDimensions().get(DimensionOptions.OVERWORLD)).chunkGenerator;
        if (!(chunkGenerator instanceof ArenaChunkGenerator arenaChunkGenerator)) {
            LasertagMod.LOGGER.warn("Cannot reload map in non-arena world");
            return;
        }

        // Start time measurement
        var blockPlaceStartTime = System.currentTimeMillis();

        // Step 1: Teleport all players back to origin for their own safety
        this.teleportPlayersToOrigin();

        try {
            // Step 2: Remove all blocks of the old arena
            var oldArenaType = arenaChunkGenerator.getConfig().getType();
            var oldArenaProceduralType = arenaChunkGenerator.getConfig().getProceduralType();
            var oldArenaBounds = this.calculateBounds(oldArenaType, oldArenaProceduralType);
            this.removeOldBlocks(oldArenaBounds);

            // Step 3: Clear spawn-point cache if necessary and reset arena structure placer
            if (!oldArenaType.equals(newArenaType)) {
                server.getLasertagServerManager().getSpawnpointManager().clearSpawnpointCache();
            }
            newArenaType.arenaPlacer.reset();

            // Step 3: Remove all entities except the players
            this.removeEntities();

            // Step 4: Set new arena chunk generator config
            arenaChunkGenerator.setConfig(new ArenaChunkGeneratorConfig(newArenaType.ordinal(), newProceduralArenaType.ordinal(), (new Random()).nextLong()));

            // Step 5: Generate new arena
            var newArenaBounds = this.calculateBounds(newArenaType, newProceduralArenaType);
            this.generateArena(newArenaBounds);

            // Step 6: Mark all changed chunks for update
            var updateBounds = this.calculateUnion(oldArenaBounds, newArenaBounds);
            this.markChunksForUpdate(updateBounds);

            // Final logging
            var blockPlaceDuration = System.currentTimeMillis() - blockPlaceStartTime;
            LasertagMod.LOGGER.info(String.format(Locale.ROOT, "Arena loaded. This took %d ms for %d chunks, or %02f ms per chunk", blockPlaceDuration, updateBounds.numChunks(), (float) blockPlaceDuration / (float) updateBounds.numChunks()));
        } finally {
            // In case of unexpected exception: Close all loading screens on the clients
            this.sendMapLoadProgressEvent("", -1.0);
        }
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    //endregion

    //region Private methods

    /**
     * Teleports all players in the world to the origin.
     * Ignores offline players.
     */
    private void teleportPlayersToOrigin() {
        LasertagGameManager.getInstance().getPlayerManager().forEachPlayer(playerUuid -> {
            var player = server.getPlayerManager().getPlayer(playerUuid);

            // Sanity check
            if (player == null) {
                // Don't handle offline players
                return;
            }

            player.requestTeleport(0.5F, 1, 0.5F);
        });
    }

    /**
     * Removes all blocks int the given arena bounds
     *
     * @param oldArenaBounds The bounds in which to remove all blocks
     */
    private void removeOldBlocks(ArenaBoundsDto oldArenaBounds) {

        // Get chunk manager
        var serverWorld = server.getOverworld();
        var serverChunkManager = serverWorld.getChunkManager();
        serverChunkManager.threadedAnvilChunkStorage.verifyChunkGenerator();

        // Init progress variables
        var currentStepString = "Removing blocks from old arena";
        this.sendMapLoadProgressEvent(currentStepString, 0.0);
        var removeBlocksChunkIndex = 0;

        // Remove all blocks of the old arena
        for(var chunkZ = oldArenaBounds.startZ(); chunkZ <= oldArenaBounds.endZ(); ++chunkZ) {
            for(var chunkX = oldArenaBounds.startX(); chunkX <= oldArenaBounds.endX(); ++chunkX) {
                var chunkPos = new ChunkPos(chunkX, chunkZ);
                var worldChunk = serverChunkManager.getWorldChunk(chunkX, chunkZ, true);
                if (worldChunk != null) {

                    for (BlockPos blockPos : BlockPos.iterate(chunkPos.getStartX(), serverWorld.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), serverWorld.getTopY() - 1, chunkPos.getEndZ())) {
                        // Set block to air. Block.FORCE_STATE so that no items drop (Flowers, seeds, etc)
                        serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE);
                    }
                }

                this.sendMapLoadProgressEvent(currentStepString, (double)(++removeBlocksChunkIndex) / (double)oldArenaBounds.numChunks());
            }
        }
    }

    /**
     * Removes all entities except players from the world
     */
    private void removeEntities() {

        server.getOverworld()
            .getEntitiesByType(TypeFilter.instanceOf(Entity.class), e -> !(e instanceof PlayerEntity))
            .forEach(Entity::discard);
    }

    /**
     * Generates the new arena.<br/>
     * <br/>
     * 1. Changes the biome<br/>
     * 2. Generates the features (places the blocks)<br/>
     * 3. Spawns the entities
     *
     * @param newArenaBounds The bounds of the new arena
     */
    private void generateArena(ArenaBoundsDto newArenaBounds) {
        // Create task executor
        TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(Util.getMainWorkerExecutor(), "lasertag-loadarena");

        // Get chunk manager
        var serverWorld = server.getOverworld();
        var serverChunkManager = serverWorld.getChunkManager();

        // For every chunk generation step necessary
        for(var chunkStatus : ImmutableList.of(ChunkStatus.BIOMES, ChunkStatus.FEATURES, ChunkStatus.SPAWN)) {

            // Init progress variables
            var currentStepChunkIndex = new AtomicInteger(0);
            var currentStepString = "Executing generation step " + chunkStatus.getId();
            this.sendMapLoadProgressEvent(currentStepString, 0.0);

            // Start time measurement for this step
            var stepStartTime = System.currentTimeMillis();

            CompletableFuture<Unit> completableFuture = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, taskExecutor::send);

            for(var z = newArenaBounds.startZ(); z <= newArenaBounds.endZ(); ++z) {
                for(var x = newArenaBounds.startX(); x <= newArenaBounds.endX(); ++x) {
                    var chunkPos = new ChunkPos(x, z);
                    var worldChunk = serverChunkManager.getWorldChunk(x, z, true);
                    if (worldChunk != null) {
                        List<Chunk> chunkList = Lists.newArrayList();
                        int taskMargin = Math.max(1, chunkStatus.getTaskMargin());

                        // Don't do this weird stuff in the else-block for the spawn entities
                        // step, or otherwise the entities will try to spawn multiple times
                        if (chunkStatus == ChunkStatus.SPAWN) {
                            chunkList.add(worldChunk);
                        } else {
                            // Weird stuff needed in order for the arena generation to work
                            for (var u = chunkPos.z - taskMargin; u <= chunkPos.z + taskMargin; ++u) {
                                for (var v = chunkPos.x - taskMargin; v <= chunkPos.x + taskMargin; ++v) {
                                    var chunk = serverChunkManager.getChunk(v, u, chunkStatus.getPrevious(), true);
                                    if (chunk instanceof ReadOnlyChunk) {
                                        chunkList.add(new ReadOnlyChunk(((ReadOnlyChunk) chunk).getWrappedChunk(), true));
                                    } else if (chunk instanceof WorldChunk) {
                                        chunkList.add(new ReadOnlyChunk((WorldChunk) chunk, true));
                                    } else {
                                        chunkList.add(chunk);
                                    }
                                }
                            }
                        }

                        completableFuture = completableFuture.thenComposeAsync((unit) -> {
                            // Execute the generation step
                            var generationTask = chunkStatus.runGenerationTask(
                                    taskExecutor::send,
                                    serverWorld,
                                    serverChunkManager.getChunkGenerator(),
                                    serverWorld.getStructureTemplateManager(),
                                    serverChunkManager.getLightingProvider(),
                                    (chunk) -> {
                                        throw new UnsupportedOperationException("Not creating full chunks here");
                                    },
                                    chunkList,
                                    true)
                                .thenApply((either) -> Unit.INSTANCE);

                            // Generation step for this chunk finished -> Send progress event
                            this.sendMapLoadProgressEvent(currentStepString, (double)currentStepChunkIndex.incrementAndGet() / (double)newArenaBounds.numChunks());

                            return generationTask;
                        }, taskExecutor::send);
                    }
                }
            }

            server.runTasks(completableFuture::isDone);
            LasertagMod.LOGGER.info("Loading map - " + chunkStatus.getId() + " took " + (System.currentTimeMillis() - stepStartTime) + " ms");
        }
    }

    /**
     * Marks all chunks in the given bounds for update.
     * Also sends the final progress event to the clients, so they can close the loading screen.
     *
     * @param bounds The bounds
     */
    private void markChunksForUpdate(ArenaBoundsDto bounds) {
        // Init progress variables
        var currentStepString = "Marking chunks for update";
        this.sendMapLoadProgressEvent(currentStepString, 0.0);
        var markUpdateChunkIndex = 0;

        // Get chunk manager
        var serverWorld = server.getOverworld();
        var serverChunkManager = serverWorld.getChunkManager();

        // For every chunk
        for(var z = bounds.startZ(); z <= bounds.endZ(); ++z) {
            for(var x = bounds.startX(); x <= bounds.endX(); ++x) {
                var chunkPos = new ChunkPos(x, z);
                var worldChunk = serverChunkManager.getWorldChunk(x, z, false);

                if (worldChunk != null) {

                    // Reload chunks on the clients to update biome
                    for (var player : serverWorld.getPlayers()) {
                        player.sendUnloadChunkPacket(chunkPos);
                        serverChunkManager.threadedAnvilChunkStorage.sendChunkDataPackets(player, new MutableObject<>(), worldChunk);
                    }

                    // Mark every block for update
                    for (var blockPos : BlockPos.iterate(chunkPos.getStartX(), serverWorld.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), serverWorld.getTopY() - 1, chunkPos.getEndZ())) {
                        serverChunkManager.markForUpdate(blockPos);
                    }
                }

                this.sendMapLoadProgressEvent(currentStepString, (double)(++markUpdateChunkIndex) / (double)bounds.numChunks());
            }
        }

        // Send final progress event with progress=-1 to close the loading screens
        currentStepString = "";
        this.sendMapLoadProgressEvent(currentStepString, -1.0);
    }

    /**
     * Calculates the chunk bounds (startX, startZ, endX, endZ and number of chunks)
     * for the given arena type
     *
     * @param arenaType The arena type to calculate the bounds for
     * @return The calculated arena bounds dto
     */
    private ArenaBoundsDto calculateBounds(ArenaType arenaType, ProceduralArenaType proceduralArenaType) {

        var arenaSize = arenaType.getArenaSize(proceduralArenaType);
        Vec3i arenaOffset = arenaType.getCompleteOffset(proceduralArenaType);
        var startZ = -arenaOffset.getZ();
        var startX = -arenaOffset.getX();
        var endZ = startZ + arenaSize.getZ();
        var endX = startX + arenaSize.getX();
        var startChunkZ = ChunkSectionPos.getSectionCoord(startZ);
        var startChunkX = ChunkSectionPos.getSectionCoord(startX);
        var endChunkZ = ChunkSectionPos.getSectionCoord(endZ);
        var endChunkX = ChunkSectionPos.getSectionCoord(endX);
        var numChunks = (endChunkZ - startChunkZ + 1) * (endChunkX - startChunkX + 1);

        return new ArenaBoundsDto(startChunkX, startChunkZ, endChunkX, endChunkZ, numChunks);
    }

    /**
     * Calculates the union bounds of two arena bounds
     *
     * @param first The first arena bounds
     * @param second The second arena bounds
     * @return The union of both bounds
     */
    private ArenaBoundsDto calculateUnion(ArenaBoundsDto first, ArenaBoundsDto second) {
        var unionStartChunkZ = Math.min(first.startZ(), second.startZ());
        var unionStartChunkX = Math.min(first.startX(), second.startX());
        var unionEndChunkZ = Math.max(first.endZ(), second.endZ());
        var unionEndChunkX = Math.max(first.endX(), second.endX());
        var unionNumChunks = (unionEndChunkZ - unionStartChunkZ + 1) * (unionEndChunkX - unionStartChunkX + 1);

        return new ArenaBoundsDto(unionStartChunkX, unionStartChunkZ, unionEndChunkX, unionEndChunkZ, unionNumChunks);
    }

    /**
     * Sends the progress event to all clients in the world
     *
     * @param stepString String describing what the current step is
     * @param progress The progress in percent of the current step
     */
    private void sendMapLoadProgressEvent(String stepString, double progress) {
        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(stepString);
        buf.writeDouble(progress);

        ServerEventSending.sendToEveryone(this.server.getOverworld(), NetworkingConstants.MAP_LOADING_EVENT, buf);
    }

    //endregion
}
