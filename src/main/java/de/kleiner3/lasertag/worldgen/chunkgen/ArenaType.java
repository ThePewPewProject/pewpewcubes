package de.kleiner3.lasertag.worldgen.chunkgen;

import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

/**
 * Enum to represent all possible arena types
 *
 * @author Étienne Muser
 */
public enum ArenaType {
    JUNGLE(BiomeKeys.JUNGLE,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/jungle_arena.litematic"),
            new Vec3i(21, 3, 61)),
    DESERT(BiomeKeys.DESERT,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/desert_arena.litematic"),
            new Vec3i(55, 1, 99)),
    FLOWER_FOREST(BiomeKeys.JUNGLE,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/jungle_arena.litematic"),
            new Vec3i(0, 0, 0));

    /**
     * Enum Constructor
     *
     * @param biome The biome for the arena (BiomeKeys.YOUR_BIOME)
     * @param nbtFileId The id of the nbt file where the arena data is stored. Size has to be >0 in x, y and z! If this is a .litematic file the used region has to be called "main"
     * @param placementOffset The offset with which to place the arena. Insert the coordinates of the world spawn point block so that this block is 0, 0, 0
     *                        (To calculate: Generate arena with offset 0,0,0 and use the targeted block coordinates of the desired spawn point block as the offset)
     */
    ArenaType(RegistryKey<Biome> biome, Identifier nbtFileId, Vec3i placementOffset) {
        this.biome = biome;
        this.nbtFileId = nbtFileId;
        this.placementOffset = placementOffset;
    }

    public final RegistryKey<Biome> biome;
    public final  Identifier nbtFileId;
    public final  Vec3i placementOffset;
}
