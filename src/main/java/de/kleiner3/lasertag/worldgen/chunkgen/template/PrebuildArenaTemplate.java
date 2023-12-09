package de.kleiner3.lasertag.worldgen.chunkgen.template;

import de.kleiner3.lasertag.common.util.NbtUtil;
import de.kleiner3.lasertag.resource.ResourceManagers;
import de.kleiner3.lasertag.resource.StructureResourceManager;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ArenaType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.io.IOException;

/**
 * Class containing all necessary information and methods for arena generation.
 *
 * @author Étienne Muser
 */
public class PrebuildArenaTemplate extends ArenaTemplate {

    private final StructureTemplate arenaTemplate;
    protected final Vec3i arenaSize;
    protected final Vec3i placementOffset;

    public PrebuildArenaTemplate(ArenaType arenaType) {

        super(false);
        this.arenaTemplate = getArenaTemplate(arenaType);
        this.arenaSize = arenaType.arenaSize;
        this.placementOffset = arenaType.placementOffset;
    }

    public StructureTemplate getArenaTemplate() {
        return arenaTemplate;
    }

    @Override
    public Vec3i getArenaSize() {
        return arenaSize;
    }

    @Override
    public Vec3i getPlacementOffset() {
        return placementOffset;
    }

    public BlockPos getStartPos() {
        return BlockPos.ORIGIN.subtract(this.placementOffset);
    }

    /**
     * Loads and converts the nbt file of the given arena type
     *
     * @param arenaType The arena type to get the template for
     * @return The arena structure template
     */
    private static StructureTemplate getArenaTemplate(ArenaType arenaType) {

        var nbtFileId = arenaType.nbtFileId;

        // Get nbt file
        var resource = ResourceManagers.STRUCTURE_RESOURCE_MANAGER.get(nbtFileId);

        // Sanity check
        if (resource == null) {
            throw new RuntimeException("Arena nbt file '" + nbtFileId.getPath() + "' not in resource manager.");
        }

        // Read nbt file
        NbtCompound nbt;
        try {
            nbt = NbtIo.readCompressed(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load nbt file '" + nbtFileId.getPath() + "'.");
        }

        // If is litematic file
        if (nbtFileId.getPath().endsWith(StructureResourceManager.LITEMATIC_FILE_ENDING)) {

            // Convert litematic nbt compound to nbt nbt compound
            nbt = NbtUtil.convertLitematicToNbt(nbt, "main", arenaType.placementOffset);

            // Sanity check
            if (nbt == null) {
                throw new RuntimeException("Litematica file '" + nbtFileId.getPath() + "' could not be converted to nbt.");
            }
        }

        // Create structure template
        var template = new StructureTemplate();
        template.readNbt(nbt);

        return template;
    }
}
