package de.kleiner3.lasertag.worldgen.chunkgen;

import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Class for registering all chunk generators
 *
 * @author Étienne Muser
 */
public class ChunkGenerators {
    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier(LasertagMod.ID, "arena_chunk_generator"), ArenaChunkGenerator.CODEC);
    }
}
