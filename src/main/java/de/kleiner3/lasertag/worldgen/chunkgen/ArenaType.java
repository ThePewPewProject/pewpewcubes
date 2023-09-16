package de.kleiner3.lasertag.worldgen.chunkgen;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.client.SoundEvents;
import de.kleiner3.lasertag.common.util.NbtUtil;
import de.kleiner3.lasertag.resource.ResourceManagers;
import de.kleiner3.lasertag.resource.StructureResourceManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.io.IOException;

/**
 * Enum to represent all possible arena types
 *
 * @author Étienne Muser
 */
public enum ArenaType {
    PROCEDURAL(BiomeKeys.THE_VOID,
            new Identifier(LasertagMod.ID, "structures/procedural_arenas"),
            null,
            "arenaType.procedural",
            new ProceduralArenaStructurePlacer(),
            SoundEvents.PROCEDURAL_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.PROCEDURAL_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.PROCEDURAL_ARENA_OUTRO_MUSIC_SOUND_EVENT),
    JUNGLE(BiomeKeys.JUNGLE,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/jungle_arena.litematic"),
            new Vec3i(22, 3, 61),
            "arenaType.jungle",
            SoundEvents.JUNGLE_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.JUNGLE_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.JUNGLE_ARENA_OUTRO_MUSIC_SOUND_EVENT),
    DESERT(BiomeKeys.DESERT,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/desert_arena.litematic"),
            new Vec3i(55, 26, 99),
            "arenaType.desert",
            SoundEvents.DESERT_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.DESERT_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.DESERT_ARENA_OUTRO_MUSIC_SOUND_EVENT),
    FLOWER_FOREST(BiomeKeys.FLOWER_FOREST,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/flower_forest_arena.litematic"),
            new Vec3i(49, 2, 70),
            "arenaType.flower_forest",
            SoundEvents.FLOWER_FOREST_ARENA_INTRO_MUSIC_SOUND_EVENT,
            SoundEvents.FLOWER_FOREST_ARENA_MUSIC_SOUND_EVENT,
            SoundEvents.FLOWER_FOREST_ARENA_OUTRO_MUSIC_SOUND_EVENT),
    MEDIEVAL_CITY(BiomeKeys.PLAINS,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/medieval_city_arena.litematic"),
            new Vec3i(116, 13, 73),
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
              String translatableName,
              SoundEvent introMusic,
              SoundEvent music,
              SoundEvent outroMusic) {
        this(biome, nbtFileId, placementOffset, translatableName, new ArenaStructurePlacer(), introMusic, music, outroMusic);
    }

    ArenaType(RegistryKey<Biome> biome,
              Identifier nbtFileId,
              Vec3i placementOffset,
              String translatableName,
              ArenaStructurePlacer arenaPlacer,
              SoundEvent introMusic,
              SoundEvent music,
              SoundEvent outroMusic) {
        this.biome = biome;
        this.nbtFileId = nbtFileId;
        this.placementOffset = placementOffset;
        this.translatableName = translatableName;
        this.arenaPlacer = arenaPlacer;
        this.introMusic = introMusic;
        this.outroMusic = outroMusic;
        this.music = music;
    }

    public final RegistryKey<Biome> biome;
    public final  Identifier nbtFileId;
    public final  Vec3i placementOffset;
    public final String translatableName;

    public final ArenaStructurePlacer arenaPlacer;

    public final SoundEvent introMusic;
    public final SoundEvent music;
    public final SoundEvent outroMusic;

    private StructureTemplate arenaTemplate = null;
    private Vec3i arenaSize = null;

    public StructureTemplate getArenaTemplate() {
        // If is procedural arena
        if (placementOffset == null) {
            throw new UnsupportedOperationException("Cannot get template of procedural arena");
        }

        if (arenaTemplate == null) {
            initArenaTemplate();
        }

        return arenaTemplate;
    }

    public Vec3i getArenaSize(ProceduralArenaType proceduralArenaType) {
        // If is procedural arena
        if (placementOffset == null) {
            return ((ProceduralArenaStructurePlacer)this.arenaPlacer).getCompleteSize(proceduralArenaType);
        }

        if (arenaSize == null) {
            initArenaTemplate();
        }

        return arenaSize;
    }

    public Vec3i getCompleteOffset(ProceduralArenaType proceduralArenaType) {
        // If is procedural arena
        if (placementOffset == null) {
            return ((ProceduralArenaStructurePlacer)this.arenaPlacer).getCompleteOffset(proceduralArenaType);
        }

        return this.placementOffset;
    }

    /**
     * Initializes the arena template with the information of the arena nbt file.
     */
    private void initArenaTemplate() {
        // Get nbt file
        var resource = ResourceManagers.STRUCTURE_RESOURCE_MANAGER.get(nbtFileId);

        // Sanity check
        if (resource == null) {
            LasertagMod.LOGGER.warn("Arena nbt file not in resource manager.");
            return;
        }

        // Read nbt file
        NbtCompound nbt;
        try {
            nbt = NbtIo.readCompressed(resource.getInputStream());
        } catch (IOException e) {
            LasertagMod.LOGGER.error("Unable to load nbt file.", e);
            return;
        }

        // If is litematic file
        if (nbtFileId.getPath().endsWith(StructureResourceManager.LITEMATIC_FILE_ENDING)) {
            // Convert litematic nbt compound to nbt nbt compound
            nbt = NbtUtil.convertLitematicToNbt(nbt, "main");

            // Sanity check
            if (nbt == null) {
                LasertagMod.LOGGER.error("Litematica file could not be converted to nbt.");
            }
        }

        this.arenaTemplate = new StructureTemplate();
        this.arenaTemplate.readNbt(nbt);
        this.arenaSize = arenaTemplate.getSize();
    }
}
