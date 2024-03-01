package de.kleiner3.lasertag.worldgen.chunkgen.type;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.client.SoundEvents;
import de.kleiner3.lasertag.worldgen.chunkgen.placer.ArenaStructurePlacer;
import de.kleiner3.lasertag.worldgen.chunkgen.placer.ProceduralArenaStructurePlacer;
import net.minecraft.sound.SoundEvent;
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
    PROCEDURAL(BiomeKeys.THE_VOID,
            new Identifier(LasertagMod.ID, "structures/procedural_arenas"),
            null,
            null,
            "arenaType.procedural",
            new ProceduralArenaStructurePlacer(),
            SoundEvents.PROCEDURAL_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.PROCEDURAL_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.PROCEDURAL_ARENA_OUTRO_MUSIC_SOUND_EVENT),
    JUNGLE(BiomeKeys.JUNGLE,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/jungle_arena.litematic"),
            new Vec3i(21, 3, 61),
            new Vec3i(144, 65, 138),
            "arenaType.jungle",
            SoundEvents.JUNGLE_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.JUNGLE_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.JUNGLE_ARENA_OUTRO_MUSIC_SOUND_EVENT),
    DESERT(BiomeKeys.DESERT,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/desert_arena.litematic"),
            new Vec3i(55, 26, 99),
            new Vec3i(117, 75, 179),
            "arenaType.desert",
            SoundEvents.DESERT_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.DESERT_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.DESERT_ARENA_OUTRO_MUSIC_SOUND_EVENT),
    FLOWER_FOREST(BiomeKeys.FLOWER_FOREST,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/flower_forest_arena.litematic"),
            new Vec3i(112, 1, 128),
            new Vec3i(295, 56, 273),
            "arenaType.flower_forest",
            SoundEvents.FLOWER_FOREST_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.FLOWER_FOREST_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.FLOWER_FOREST_ARENA_OUTRO_MUSIC_SOUND_EVENT),
    MEDIEVAL_CITY(BiomeKeys.PLAINS,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/medieval_city_arena.litematic"),
            new Vec3i(116, 13, 73),
            new Vec3i(290, 85, 357),
            "arenaType.medieval_city",
            SoundEvents.MEDIEVAL_CITY_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.MEDIEVAL_CITY_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.MEDIEVAL_CITY_ARENA_OUTRO_MUSIC_SOUND_EVENT);

    /**
     * Enum Constructor
     *
     * @param biome The biome for the arena (BiomeKeys.YOUR_BIOME)
     * @param nbtFileId The id of the nbt file where the arena data is stored. Size has to be >0 in x, y and z! If this is a .litematic file the used region has to be called "main"
     * @param placementOffset The offset with which to place the arena. Insert the coordinates of the world spawn point block so that this block is 0, 0, 0
     *                        (To calculate: Generate arena with offset 0,0,0 and use the targeted block coordinates of the desired spawn point block as the offset)
     * @param translatableName The name used for translation
     */
    ArenaType(RegistryKey<Biome> biome,
              Identifier nbtFileId,
              Vec3i placementOffset,
              Vec3i arenaSize,
              String translatableName,
              SoundEvent introMusic,
              SoundEvent music,
              SoundEvent outroMusic) {
        this(biome, nbtFileId, placementOffset, arenaSize, translatableName, new ArenaStructurePlacer(), introMusic, music, outroMusic);
    }

    ArenaType(RegistryKey<Biome> biome,
              Identifier nbtFileId,
              Vec3i placementOffset,
              Vec3i arenaSize,
              String translatableName,
              ArenaStructurePlacer arenaPlacer,
              SoundEvent introMusic,
              SoundEvent music,
              SoundEvent outroMusic) {
        this.biome = biome;
        this.nbtFileId = nbtFileId;
        this.placementOffset = placementOffset;
        this.arenaSize = arenaSize;
        this.translatableName = translatableName;
        this.arenaPlacer = arenaPlacer;
        this.introMusic = introMusic;
        this.outroMusic = outroMusic;
        this.music = music;
    }

    public final RegistryKey<Biome> biome;
    public final Identifier nbtFileId;
    public final Vec3i placementOffset;
    public final Vec3i arenaSize;
    public final String translatableName;

    public final ArenaStructurePlacer arenaPlacer;

    public final SoundEvent introMusic;
    public final SoundEvent music;
    public final SoundEvent outroMusic;
}
