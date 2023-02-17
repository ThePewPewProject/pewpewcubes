package de.kleiner3.lasertag.worldgen.chunkgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;

/**
 * The jungle arena
 *
 * @author Étienne Muser
 */
public class JungleArenaChunkGenerator extends ArenaChunkGenerator {
    public static final Codec<JungleArenaChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> JungleArenaChunkGenerator.createStructureSetRegistryGetter(instance).and(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(chunkGenerator -> chunkGenerator.biomeRegistry)).apply(instance, instance.stable(JungleArenaChunkGenerator::new)));

    private final Registry<Biome> biomeRegistry;

    public JungleArenaChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry) {
        super(structureSetRegistry, new FixedBiomeSource(biomeRegistry.getOrCreateEntry(BiomeKeys.JUNGLE)), new Identifier(LasertagMod.ID, "structures/prebuild_arenas/jungle_arena.litematic"), new Vec3i(21, 3, 61));

        this.biomeRegistry = biomeRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
}
