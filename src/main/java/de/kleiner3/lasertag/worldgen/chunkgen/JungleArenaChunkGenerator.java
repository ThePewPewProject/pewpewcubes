package de.kleiner3.lasertag.worldgen.chunkgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtIo;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class JungleArenaChunkGenerator extends ChunkGenerator {
    public static final Codec<JungleArenaChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> JungleArenaChunkGenerator.createStructureSetRegistryGetter(instance).and(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(chunkGenerator -> chunkGenerator.biomeRegistry)).apply(instance, instance.stable(JungleArenaChunkGenerator::new)));

    private final Registry<Biome> biomeRegistry;

    private final Supplier<List<PlacedFeatureIndexer.IndexedFeatures>> indexedFeaturesListSupplier;

    private final Identifier nbtFileId = new Identifier(LasertagMod.ID, "structures/jungle_arena/main.nbt");
    private StructureTemplate structureTemplate = null;
    private StructurePlacementData basePlacementData = null;
    private Vec3i size = null;
    private BlockPos startPos = null;

    public JungleArenaChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry) {
        super(structureSetRegistry, Optional.empty(), new FixedBiomeSource(biomeRegistry.getOrCreateEntry(BiomeKeys.JUNGLE)));

        this.biomeRegistry = biomeRegistry;
        this.indexedFeaturesListSupplier = Suppliers.memoize(() -> PlacedFeatureIndexer.collectIndexedFeatures(List.copyOf(biomeSource.getBiomes()), biomeEntry -> new ArrayList<>(), true));

        // Read nbt file
        var resource = LasertagMod.STRUCTURE_RESOURCE_MANAGER.get(nbtFileId);

        if (resource == null) {
            LasertagMod.LOGGER.error("Arena nbt file not in resource manager.");
            return;
        }

        try {
            var nbt = NbtIo.readCompressed(resource.getInputStream());
            this.structureTemplate = new StructureTemplate();
            this.structureTemplate.readNbt(nbt);
            this.basePlacementData = new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(false);
            this.size = structureTemplate.getSize();
            this.startPos = BlockPos.ORIGIN;
        } catch (IOException e) {
            LasertagMod.LOGGER.error("Unable to load nbt file.", e);
        }
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        var bBox = getBlockBoxForChunk(chunk);

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
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
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
