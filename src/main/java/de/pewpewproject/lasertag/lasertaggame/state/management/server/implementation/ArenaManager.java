package de.pewpewproject.lasertag.lasertaggame.state.management.server.implementation;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.common.util.ThreadUtil;
import de.pewpewproject.lasertag.lasertaggame.arena.ArenaBoundsDto;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.IArenaManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.IBlockTickManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.ISpawnpointManager;
import de.pewpewproject.lasertag.lasertaggame.state.synced.IPlayerNamesState;
import de.pewpewproject.lasertag.networking.NetworkingConstants;
import de.pewpewproject.lasertag.networking.server.ServerEventSending;
import de.pewpewproject.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import de.pewpewproject.lasertag.worldgen.chunkgen.ArenaChunkGeneratorConfig;
import de.pewpewproject.lasertag.worldgen.chunkgen.template.ArenaTemplate;
import de.pewpewproject.lasertag.worldgen.chunkgen.template.TemplateRegistry;
import de.pewpewproject.lasertag.worldgen.chunkgen.type.ArenaType;
import de.pewpewproject.lasertag.worldgen.chunkgen.type.ProceduralArenaType;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * Implementation of IArenaManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class ArenaManager implements IArenaManager {

    private static final long UPDATE_FREQUENCY = 200;

    private final MinecraftServer server;
    private final ISpawnpointManager spawnpointManager;
    private final IPlayerNamesState playerNamesState;
    private final IBlockTickManager blockTickManager;

    private boolean isLoading = false;
    private long lastStatusUpdateTime = 0;

    //private CompletableFuture<Void> arenaGenerationFuture;

    public ArenaManager(MinecraftServer server,
                        ISpawnpointManager spawnpointManager,
                        IPlayerNamesState playerNamesState,
                        IBlockTickManager blockTickManager) {
        this.server = server;
        this.spawnpointManager = spawnpointManager;
        this.playerNamesState = playerNamesState;
        this.blockTickManager = blockTickManager;
        //arenaGenerationFuture = CompletableFuture.completedFuture(null);
    }

    //region Public methods

    @Override
    public boolean loadArena(ArenaType newArenaType, ProceduralArenaType newProceduralArenaType) {

        return loadArena(newArenaType, newProceduralArenaType, true);
    }

    @Override
    public boolean reloadArena() {

        // Check if this is an arena world
        var chunkGenerator = Objects.requireNonNull(server.getSaveProperties()
                .getGeneratorOptions()
                .getDimensions()
                .get(DimensionOptions.OVERWORLD))
                .chunkGenerator;

        // If the chunk generator is not an arena chunk generator
        if (!(chunkGenerator instanceof ArenaChunkGenerator arenaChunkGenerator)) {
            LasertagMod.LOGGER.warn("Cannot reload map in non-arena world");
            return false;
        }

        // Get the arena types
        var lastArenaType = arenaChunkGenerator.getConfig().getType();
        var lastProceduralArenaType = arenaChunkGenerator.getConfig().getProceduralType();

        return loadArena(lastArenaType, lastProceduralArenaType, false);
    }

    @Override
    public CompletableFuture<Void> getLoadArenaFuture() {
        //
        return CompletableFuture.completedFuture(null);
    }

    private boolean loadArena(ArenaType newArenaType, ProceduralArenaType newProceduralArenaType, boolean sendPlayersToOrigin) {

        // Start the generation
        LasertagMod.LOGGER.info("Starting to load new arena '" + newArenaType.translatableName + "(" + newProceduralArenaType.translatableName + ")'");

        // Check if this is an arena world
        var chunkGenerator = Objects.requireNonNull(server.getSaveProperties()
                .getGeneratorOptions()
                .getDimensions()
                .get(DimensionOptions.OVERWORLD))
                .chunkGenerator;

        // If the chunk generator is not an arena chunk generator
        if (!(chunkGenerator instanceof ArenaChunkGenerator arenaChunkGenerator)) {
            LasertagMod.LOGGER.warn("Cannot reload map in non-arena world");
            return false;
        }

        var executor = ThreadUtil.createScheduledExecutor("arena-load-thread");
        executor.execute(() -> loadArenaInner(arenaChunkGenerator, newArenaType, newProceduralArenaType, sendPlayersToOrigin));

        executor.shutdown();

        return true;
    }

    private void loadArenaInner(ArenaChunkGenerator arenaChunkGenerator,
                                ArenaType newArenaType,
                                ProceduralArenaType newProceduralArenaType,
                                boolean sendPlayersToOrigin) {

        try {
            // Disable saving of the world
            server.getOverworld().savingDisabled = true;

            // Show loading screen
            this.sendMapLoadProgressEvent("Starting the generation of the arena", 0, false);
            this.isLoading = true;

            // Create old arena template
            var oldArenaType = arenaChunkGenerator.getConfig().getType();
            var oldArenaProceduralType = arenaChunkGenerator.getConfig().getProceduralType();
            var oldArenaTemplate = TemplateRegistry.getTemplate(oldArenaType, oldArenaProceduralType, arenaChunkGenerator.getConfig().getSeed());

            // Start time measurement
            var blockPlaceStartTime = System.currentTimeMillis();

            if (sendPlayersToOrigin) {

                // Teleport all players back to origin for their own safety
                teleportPlayersToOrigin();
            }

            // Remove all blocks of the old arena
            var oldArenaBounds = this.calculateBounds(oldArenaTemplate);
            removeOldBlocks(oldArenaBounds);

            // Clear spawn-point cache if necessary and reset arena structure placer
            if (!oldArenaType.equals(newArenaType) || oldArenaType.equals(ArenaType.PROCEDURAL)) {
                spawnpointManager.clearSpawnpointCache();
            }

            // Remove all entities except the players
            removeEntities();

            // Set new arena chunk generator config
            var newSeed = new Random().nextLong();
            arenaChunkGenerator.setConfig(new ArenaChunkGeneratorConfig(newArenaType.ordinal(), newProceduralArenaType.ordinal(), newSeed));
            var newArenaTemplate = TemplateRegistry.getTemplate(newArenaType, newProceduralArenaType, newSeed);

            // Generate new arena
            var newArenaBounds = this.calculateBounds(newArenaTemplate);
            generateArena(arenaChunkGenerator, newArenaBounds);

            // Set biomes
            setBiomes(newArenaBounds, arenaChunkGenerator);

            if (sendPlayersToOrigin) {

                // Teleport players back to origin
                teleportPlayersToOrigin();
            }

            // Calculate the union of the new and old arena
            var updateBounds = this.calculateUnion(oldArenaBounds, newArenaBounds);

            // Final logging
            var blockPlaceDuration = System.currentTimeMillis() - blockPlaceStartTime;
            LasertagMod.LOGGER.info(String.format(Locale.ROOT, "Arena loaded. This took %d ms for %d chunks, or %02f ms per chunk", blockPlaceDuration, updateBounds.numChunks(), (float) blockPlaceDuration / (float) updateBounds.numChunks()));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Exception while loading arena:", ex);
        } finally {
            // Stop loading
            this.sendMapLoadProgressEvent("", -1.0, false);
            this.isLoading = false;

            // Disable saving of the world
            server.getOverworld().savingDisabled = false;
        }
    }

    @Override
    public boolean isLoading() {
        return this.isLoading;
    }

    //endregion

    //region Private methods

    /**
     * Teleports all players in the world to the origin.
     * Ignores offline players.
     */
    private void teleportPlayersToOrigin() {

            playerNamesState.forEachPlayer(playerUuid -> {
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

        // Reset custom block tickers
        blockTickManager.clear();

        // Get chunk manager
        var serverWorld = server.getOverworld();
        var serverChunkManager = serverWorld.getChunkManager();

        // Init progress variables
        var currentStepString = "Removing blocks from old arena";
        this.sendMapLoadProgressEvent(currentStepString, 0.0, true);
        var removeBlocksChunkIndex = new AtomicInteger(0);

        // Remove all blocks of the old arena
        serverWorld.fastChunkIter(chunk -> {

            // Get the chunk position
            var chunkPos = chunk.getPos();

            // Get the block positions of every block in the chunk
            var blockPositions = BlockPos.iterate(chunkPos.getStartX(),
                    serverWorld.getBottomY(),
                    chunkPos.getStartZ(),
                    chunkPos.getEndX(),
                    serverWorld.getTopY() - 1,
                    chunkPos.getEndZ());

            // For every block in the chunk
            for (var blockPos : blockPositions) {

                var x = blockPos.getX();
                var y = blockPos.getY();
                var z = blockPos.getZ();

                // Set block to air.
                serverWorld.fastSetBlock(chunk, blockPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE);
                //chunk.setBlockState(blockPos, Blocks.AIR.getDefaultState(), false);
                //chunk.getSection(chunk.getSectionIndex(y)).setBlockState(x & 15, y & 15, z & 15, Blocks.AIR.getDefaultState(), false);
            }

            chunk.clear();
            //chunk.setNeedsSaving(true);
        },
                (c, m) -> sendMapLoadProgressEvent(currentStepString, (double)c / m, true),
                oldArenaBounds.startX(),
                oldArenaBounds.endX(),
                oldArenaBounds.startZ(),
                oldArenaBounds.endZ());
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
     * Generates the new arena.
     *
     * @param newArenaBounds The bounds of the new arena
     */
    private void generateArena(ArenaChunkGenerator chunkGenerator,
                               ArenaBoundsDto newArenaBounds) {

            // Get chunk manager
            var serverWorld = server.getOverworld();
            var serverChunkManager = serverWorld.getChunkManager();
            serverChunkManager.threadedAnvilChunkStorage.verifyChunkGenerator();

            // Init progress variables
            var currentStepString = "Generating new arena";
            this.sendMapLoadProgressEvent(currentStepString, 0.0, true);

            // Start time measurement for this step
            var stepStartTime = System.currentTimeMillis();

            // For each chunk
            serverWorld.fastChunkIter(worldChunk -> {

                        // Generate arena
                        chunkGenerator.getConfig().getType().arenaPlacer.placeArenaChunkSegment(chunkGenerator.getTemplate(),
                                worldChunk,
                                serverWorld,
                                false);

                        // Spawn entities
                        chunkGenerator.populateEntities(new ChunkRegion(serverWorld,
                                List.of(worldChunk),
                                ChunkStatus.FULL,
                                -1));
                    },
                    (c, m) -> sendMapLoadProgressEvent(currentStepString, (double) c / m, true),
                    newArenaBounds.startX(),
                    newArenaBounds.endX(),
                    newArenaBounds.startZ(),
                    newArenaBounds.endZ());

            // Final logging
            LasertagMod.LOGGER.info("Generating Arena took " + (System.currentTimeMillis() - stepStartTime) + " ms");
    }

    /**
     * Sets the biomes of the chunks of the new arena
     *
     * @param newArenaBounds      The bounds of the new arena
     * @param arenaChunkGenerator The arena chunk generator
     */
    private void setBiomes(ArenaBoundsDto newArenaBounds, ChunkGenerator arenaChunkGenerator) {

            // Init progress variables
            var currentStepString = "Setting biomes";
            this.sendMapLoadProgressEvent(currentStepString, 0.0, true);
            var markUpdateChunkIndex = new AtomicInteger(0);

            // Get chunk manager
            var serverWorld = server.getOverworld();
            var serverChunkManager = serverWorld.getChunkManager();

            // For each chunk
            forEachChunk(newArenaBounds, (chunkX, chunkZ) -> {

                var chunkPos = new ChunkPos(chunkX, chunkZ);
                var worldChunk = serverChunkManager.getWorldChunk(chunkX, chunkZ, false);

                // If the chunk could not be retrieved
                if (worldChunk == null) {
                    LasertagMod.LOGGER.warn("Load arena, set biomes - Could not get chunk (" + chunkX + ", " + chunkZ + ")");
                    return;
                }

                // Set the biome
                worldChunk.populateBiomes(arenaChunkGenerator.getBiomeSource(),
                        serverWorld.getChunkManager().getNoiseConfig().getMultiNoiseSampler());

                // Reload chunks on the clients to update biome
                for (var player : serverWorld.getPlayers()) {

                    player.sendUnloadChunkPacket(chunkPos);
                    serverChunkManager.threadedAnvilChunkStorage
                            .sendChunkDataPackets(player, new MutableObject<>(), worldChunk);
                }

                this.sendMapLoadProgressEvent(currentStepString,
                        (double) (markUpdateChunkIndex.incrementAndGet()) / (double) newArenaBounds.numChunks(), true);
            });
    }

    /**
     * Calculates the chunk bounds (startX, startZ, endX, endZ and number of chunks)
     * for the given arena type
     *
     * @param template The arena template to calculate the bounds for
     * @return The calculated arena bounds dto
     */
    private ArenaBoundsDto calculateBounds(ArenaTemplate template) {

        var arenaSize = template.getArenaSize();
        Vec3i arenaOffset = template.getPlacementOffset();
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
     * @param first  The first arena bounds
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
     * @param progress   The progress in percent of the current step
     */
    private void sendMapLoadProgressEvent(String stepString, double progress, boolean throttled) {

        if (throttled) {

            // Get the current time
            var currTime = System.currentTimeMillis();

            if (currTime - lastStatusUpdateTime < UPDATE_FREQUENCY) {
                return;
            }

            lastStatusUpdateTime = currTime;
        }

        LasertagMod.LOGGER.info("Progress '{}': {}", stepString, progress);

        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(stepString);
        buf.writeDouble(progress);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.MAP_LOADING_EVENT, buf);
    }

    /**
     * Executes a consumer on every chunk in the given bounds
     *
     * @param bounds The bounds
     * @param action The action to execute. First argument is the chunk x-position, second the chunk z-position
     */
    private void forEachChunk(ArenaBoundsDto bounds, BiConsumer<Integer, Integer> action) {

        // For every slice of chunks in z-direction
        for (var chunkZ = bounds.startZ(); chunkZ <= bounds.endZ(); ++chunkZ) {

            // For every chunk in the slice
            for (var chunkX = bounds.startX(); chunkX <= bounds.endX(); ++chunkX) {

                // Call the action
                action.accept(chunkX, chunkZ);
            }
        }
    }
//endregion
}
