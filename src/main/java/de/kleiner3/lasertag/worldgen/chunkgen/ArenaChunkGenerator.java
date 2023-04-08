package de.kleiner3.lasertag.worldgen.chunkgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Base class for arena chunk generators
 *
 * @author Étienne Muser
 */
public class ArenaChunkGenerator extends ChunkGenerator {
    public static final Codec<ArenaChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            createStructureSetRegistryGetter(instance)
            .and(RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                .forGetter(chunkGenerator -> chunkGenerator.biomeRegistry))
            .and(ArenaChunkGeneratorConfig.CODEC.fieldOf("settings")
                .forGetter(ArenaChunkGenerator::getConfig))
            .apply(instance, instance.stable(ArenaChunkGenerator::new)));

    private final ArenaChunkGeneratorConfig config;
    private final Registry<Biome> biomeRegistry;

    /**
     * The constructor to use to generate an arena
     * @param config The arena chunk generator config
     */
    public ArenaChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry,  ArenaChunkGeneratorConfig config) {
        super(structureSetRegistry, Optional.empty(), new FixedBiomeSource(biomeRegistry.getOrCreateEntry(config.getType().biome)));

        this.config = config;
        this.biomeRegistry = biomeRegistry;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        // Get the current chunks block box
        var bBox = getBlockBoxForChunk(chunk);

        // Get the arena chunk generator config type
        var type = config.getType();

        // Calculate the start pos
        var startPos = BlockPos.ORIGIN.subtract(type.placementOffset);

        // Get the arena size
        var size = type.getArenaSize();

        // If chunk does not intersect with arena bounding box, do nothing
        if (bBox.getMaxX() < startPos.getX() || bBox.getMinX() > (startPos.getX() + size.getX()) ||
                bBox.getMaxZ() < startPos.getZ() || bBox.getMinZ() > (startPos.getZ() + size.getZ())) {
            return;
        }

        // Place the arena
        ArenaStructurePlacer.placeArenaChunkSegment(type.getArenaTemplate(), bBox, startPos, world);
    }

    private static BlockBox getBlockBoxForChunk(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        HeightLimitView heightLimitView = chunk.getHeightLimitView();
        int startY = heightLimitView.getBottomY() + 1;
        int endY = heightLimitView.getTopY() - 1;
        return new BlockBox(startX, startY, startZ, startX + 15, endY, startZ + 15);
    }

    public ArenaChunkGeneratorConfig getConfig() {
        return config;
    }

    @Override
    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(RegistryEntry<Biome> biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        // Disable all mob spawning
        return SpawnSettings.EMPTY_ENTRY_POOL;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        // Empty
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
        // Empty
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
        // Empty
    }

    @Override
    public void populateEntities(ChunkRegion region) {

        // Place the arena entities
        ArenaStructurePlacer.spawnEntitiesOfArena(config.getType().getArenaTemplate(), BlockPos.ORIGIN.subtract(config.getType().placementOffset), region);
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

    @Override
    public Codec<ArenaChunkGenerator> getCodec() {
        return CODEC;
    }
}
