package de.kleiner3.lasertag.worldgen.chunkgen;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.NbtUtil;
import de.kleiner3.lasertag.resource.ResourceManagers;
import de.kleiner3.lasertag.resource.StructureResourceManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
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
    JUNGLE(BiomeKeys.JUNGLE,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/jungle_arena.litematic"),
            new Vec3i(22, 3, 61),
            "arenaType.jungle"),
    DESERT(BiomeKeys.DESERT,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/desert_arena.litematic"),
            new Vec3i(55, 26, 99),
            "arenaType.desert"),
    FLOWER_FOREST(BiomeKeys.FLOWER_FOREST,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/flower_forest_arena.litematic"),
            new Vec3i(49, 2, 70),
            "arenaType.flower_forest"),
    MEDIEVAL_CITY(BiomeKeys.PLAINS,
            new Identifier(LasertagMod.ID, "structures/prebuild_arenas/medieval_city_arena.litematic"),
            new Vec3i(116, 14, 73),
            "arenaType.medieval_city");

    /**
     * Enum Constructor
     *
     * @param biome The biome for the arena (BiomeKeys.YOUR_BIOME)
     * @param nbtFileId The id of the nbt file where the arena data is stored. Size has to be >0 in x, y and z! If this is a .litematic file the used region has to be called "main"
     * @param placementOffset The offset with which to place the arena. Insert the coordinates of the world spawn point block so that this block is 0, 0, 0
     *                        (To calculate: Generate arena with offset 0,0,0 and use the targeted block coordinates of the desired spawn point block as the offset)
     */
    ArenaType(RegistryKey<Biome> biome, Identifier nbtFileId, Vec3i placementOffset, String translatableName) {
        this.biome = biome;
        this.nbtFileId = nbtFileId;
        this.placementOffset = placementOffset;
        this.translatableName = translatableName;
    }

    public final RegistryKey<Biome> biome;
    public final  Identifier nbtFileId;
    public final  Vec3i placementOffset;
    public final String translatableName;

    private StructureTemplate arenaTemplate = null;
    private Vec3i arenaSize = null;

    public StructureTemplate getArenaTemplate() {
        if (arenaTemplate == null) {
            initArenaTemplate();
        }

        return arenaTemplate;
    }

    public Vec3i getArenaSize() {
        if (arenaSize == null) {
            initArenaTemplate();
        }

        return arenaSize;
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
