package de.kleiner3.lasertag.worldgen.chunkgen;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.resource.ResourceManagers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Base class for arena chunk generators
 *
 * @author Étienne Muser
 */
public abstract class ArenaChunkGenerator extends ChunkGenerator {
    // TODO: Set difficulty to peaceful

    private StructureTemplate structureTemplate;
    private StructurePlacementData basePlacementData;
    private Vec3i size;
    private BlockPos startPos;

    /**
     * The constructor to use to generate an arena
     * @param structureSetRegistry Should be BuiltinRegistries.STRUCTURE_SET
     * @param biome The biome for the arena (new FixedBiomeSource(biomeRegistry.getOrCreateEntry(BiomeKeys.YOUR_BIOME)))
     * @param nbtFileId The id of the nbt file where the arena data is stored
     * @param placementOffset The offset with which to place the arena. Insert the coordinates of the world spawn point block so that this block is 0, 0, 0
     */
    public ArenaChunkGenerator(Registry<StructureSet> structureSetRegistry, FixedBiomeSource biome, Identifier nbtFileId, Vec3i placementOffset) {
        super(structureSetRegistry, Optional.empty(), biome);

        // Read nbt file
        var resource = ResourceManagers.STRUCTURE_RESOURCE_MANAGER.get(nbtFileId);

        if (resource == null) {
            LasertagMod.LOGGER.warn("Arena nbt file not in resource manager.");
            return;
        }

        NbtCompound nbt;
        try {
            nbt = NbtIo.readCompressed(resource.getInputStream());
        } catch (IOException e) {
            LasertagMod.LOGGER.error("Unable to load nbt file.", e);
            return;
        }

        this.structureTemplate = new StructureTemplate();
        this.structureTemplate.readNbt(nbt);
        this.basePlacementData = new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(false);
        this.size = structureTemplate.getSize();
        this.startPos = BlockPos.ORIGIN.subtract(placementOffset);
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        var bBox = getBlockBoxForChunk(chunk);

        if (startPos == null) {
            LasertagMod.LOGGER.error("Error while generating arena chunk " + chunk.getPos() + ": startPos was null.");
            return;
        }

        // If chunk does not intersect with arena bounding box, do nothing
        if (bBox.getMaxX() < startPos.getX() || bBox.getMinX() > (startPos.getX() + size.getX()) ||
                bBox.getMaxZ() < startPos.getZ() || bBox.getMinZ() > (startPos.getZ() + size.getZ())) {
            return;
        }

        // Place the arena
        StructurePlacementData structurePlacementData = basePlacementData.setBoundingBox(bBox);
        structureTemplate.place(world, startPos, BlockPos.ORIGIN, structurePlacementData, world.getRandom(), 2);
    }

    private static BlockBox getBlockBoxForChunk(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        HeightLimitView heightLimitView = chunk.getHeightLimitView();
        int k = heightLimitView.getBottomY() + 1;
        int l = heightLimitView.getTopY() - 1;
        return new BlockBox(i, k, j, i + 15, l, j + 15);
    }

    @Override
    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(RegistryEntry<Biome> biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        // Disable all mob spawning
        return SpawnSettings.EMPTY_ENTRY_POOL;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
    }

    @Override
    public int getSpawnHeight(HeightLimitView world) {
        return 0;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getMinimumY() {
        return -63;
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return -63;
    }
}
