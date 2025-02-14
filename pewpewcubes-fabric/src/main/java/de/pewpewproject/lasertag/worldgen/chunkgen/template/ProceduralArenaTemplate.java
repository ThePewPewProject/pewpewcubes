package de.pewpewproject.lasertag.worldgen.chunkgen.template;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.common.types.Vec3;
import de.pewpewproject.lasertag.common.util.NbtUtil;
import de.pewpewproject.lasertag.resource.ResourceManagers;
import de.pewpewproject.lasertag.resource.StructureResourceManager;
import de.pewpewproject.lasertag.worldgen.chunkgen.type.ArenaType;
import de.pewpewproject.lasertag.worldgen.chunkgen.type.ProceduralArenaType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.Resource;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Étienne Muser
 */
public class ProceduralArenaTemplate extends ArenaTemplate {

    private static final int BLOCKS_PER_CHUNK = 16;

    // Start pos of the arena (lower left corner in block coordinates, shell not included)
    private BlockPos arenaStartPos = null;

    // The bounds of the arena in chunks (shell included)
    private Integer arenaMinChunkX = null;
    private Integer arenaMinChunkZ = null;
    private Integer arenaMaxChunkX = null;
    private Integer arenaMaxChunkZ = null;

    private Integer lobbyMinChunkX = null;
    private Integer lobbyMinChunkZ = null;
    private Integer lobbyMaxChunkX = null;
    private Integer lobbyMaxChunkZ = null;

    private Integer completeChunkXSize = null;
    private Integer completeChunkZSize = null;

    protected Vec3i completeOffset;

    private List<StructureTemplate> randomTemplates;

    private StructureTemplate lobbyTemplate = null;

    private StructureTemplate shellTemplate = null;

    private final ProceduralArenaType proceduralArenaType;

    public ProceduralArenaTemplate(ProceduralArenaType arenaType, long seed) {
        super(true);

        var lobbyStartPos = BlockPos.ORIGIN.subtract(arenaType.lobbyOffset);
        initLobbyTemplate(arenaType);
        initLobbySize(lobbyStartPos);
        initArenaSize(arenaType);
        initShellTemplate(arenaType);
        initRandomTemplates(arenaType, seed);

        this.proceduralArenaType = arenaType;
    }

    public StructureTemplate getShellTemplate() {
        return this.shellTemplate;
    }

    public StructureTemplate getLobbyTemplate() {
        return this.lobbyTemplate;
    }

    public StructureTemplate getRandomTemplate(int segmentId) {
        return this.randomTemplates.get(segmentId);
    }

    @Override
    public Vec3i getArenaSize() {
        return new Vec3i(completeChunkXSize * BLOCKS_PER_CHUNK,
                Math.max(lobbyTemplate.getSize().getY(), shellTemplate.getSize().getY()),
                completeChunkZSize * BLOCKS_PER_CHUNK);
    }

    @Override
    public Vec3i getPlacementOffset() {
        return this.completeOffset;
    }

    public ProceduralArenaType getProceduralArenaType() {
        return this.proceduralArenaType;
    }

    public boolean isLobbyChunk(int x, int z) {
        return isInside(x, z,
                lobbyMinChunkX,
                lobbyMinChunkZ,
                lobbyMaxChunkX,
                lobbyMaxChunkZ);
    }

    public boolean isArenaChunk(int x, int z) {
        return isInside(x, z,
                arenaMinChunkX,
                arenaMinChunkZ,
                arenaMaxChunkX,
                arenaMaxChunkZ);
    }

    public boolean isArenaChunkWithoutShell(int x, int z) {
        // +/- 1 offset because of shell
        return isInside(x, z,
                arenaMinChunkX + 1,
                arenaMinChunkZ + 1,
                arenaMaxChunkX - 1,
                arenaMaxChunkZ - 1);
    }

    public int getArenaMinChunkX() {
        return this.arenaMinChunkX;
    }

    public int getArenaMinChunkZ() {
        return this.arenaMinChunkZ;
    }

    private void initLobbySize(BlockPos lobbyStartPos) {

        var lobbySize = this.lobbyTemplate.getSize();
        var lobbyEndX = lobbyStartPos.getX() + lobbySize.getX();
        var lobbyEndZ = lobbyStartPos.getZ() + lobbySize.getZ();
        lobbyMinChunkX = ChunkSectionPos.getSectionCoord(lobbyStartPos.getX());
        lobbyMinChunkZ = ChunkSectionPos.getSectionCoord(lobbyStartPos.getZ());
        lobbyMaxChunkX = ChunkSectionPos.getSectionCoord(lobbyEndX);
        lobbyMaxChunkZ = ChunkSectionPos.getSectionCoord(lobbyEndZ);
    }

    private void initArenaSize(ProceduralArenaType proceduralArenaType) {

        var segments = proceduralArenaType.segments;

        int startChunkX;
        int startChunkZ;
        int arenaChunkSizeX = proceduralArenaType.segments.length;
        int arenaChunkSizeZ = proceduralArenaType.segments[0].length;
        if (lobbyMaxChunkX > lobbyMaxChunkZ) {
            startChunkZ = lobbyMaxChunkZ + 1;
            // Integer division intended
            startChunkX = -(segments.length / 2) - 1;

            completeChunkXSize = Math.max(arenaChunkSizeX + 2, lobbyMaxChunkX - lobbyMinChunkX);
            completeChunkZSize = (lobbyMaxChunkZ - lobbyMinChunkZ) + arenaChunkSizeZ + 2;
        } else {
            startChunkX = lobbyMaxChunkX + 1;
            // Integer division intended
            startChunkZ = -(segments[0].length / 2) - 1;

            completeChunkXSize = (lobbyMaxChunkX - lobbyMinChunkX) + arenaChunkSizeX + 2;
            completeChunkZSize = Math.max(arenaChunkSizeZ + 2, lobbyMaxChunkZ - lobbyMinChunkZ);
        }

        this.arenaStartPos = new BlockPos((startChunkX + 1) * BLOCKS_PER_CHUNK, 0, (startChunkZ + 1) * BLOCKS_PER_CHUNK);
        this.arenaMinChunkX = startChunkX;
        this.arenaMinChunkZ = startChunkZ;
        // Plus one for the shell
        this.arenaMaxChunkX = this.arenaMinChunkX + arenaChunkSizeX + 1;
        this.arenaMaxChunkZ = this.arenaMinChunkZ + arenaChunkSizeZ + 1;

        var completeMinChunkX = Math.min(lobbyMinChunkX, arenaMinChunkX);
        var completeMinChunkZ = Math.min(lobbyMinChunkZ, arenaMinChunkZ);
        this.completeOffset = new Vec3i(-completeMinChunkX * BLOCKS_PER_CHUNK, 0, -completeMinChunkZ * BLOCKS_PER_CHUNK);
    }

    private void initRandomTemplates(ProceduralArenaType proceduralArenaType, long seed) {

        var random = new Random(seed);

        var resourceManager = ResourceManagers.STRUCTURE_RESOURCE_MANAGER;
        var templateResources = new ArrayList<List<Vec3<Integer, Identifier, Resource>>>();

        Arrays.stream(proceduralArenaType.segments).flatMapToInt(Arrays::stream).distinct().sorted().forEach(segmentId -> {

            var resources = resourceManager.getFolder(new Identifier(LasertagMod.ID, ArenaType.PROCEDURAL.nbtFileId.getPath() + "/" + proceduralArenaType.mapFolderName + "/" + proceduralArenaType.folderNameMapping[segmentId]));

            var mappedResources = resources.stream()
                    .map(r -> new Vec3<>(segmentId, r.getKey(), r.getValue()))
                    .toList();

            templateResources.add(mappedResources);
        });


        randomTemplates = templateResources.stream()
                .map(indexTemplates -> indexTemplates.get(random.nextInt(indexTemplates.size())))
                .map(r -> {
                    var localOffset = this.getLocalChunkOffset(proceduralArenaType, r.x());
                    return initTemplate(r.y(), r.z(), BlockPos.ORIGIN.subtract(arenaStartPos.add(localOffset)));
                })
                .toList();
    }

    private void initLobbyTemplate(ProceduralArenaType proceduralArenaType) {

        var lobbyId = new Identifier(LasertagMod.ID, ArenaType.PROCEDURAL.nbtFileId.getPath() + "/" + proceduralArenaType.mapFolderName + "/lobby.litematic");
        var lobbyResource = ResourceManagers.STRUCTURE_RESOURCE_MANAGER.get(lobbyId);

        if (lobbyResource == null) {
            throw new RuntimeException("Could not find lobby resource '" + lobbyId.getPath() + "' for arena '" + proceduralArenaType.translatableName + "'");
        }

        this.lobbyTemplate = initTemplate(lobbyId, lobbyResource, proceduralArenaType.lobbyOffset);
    }

    private void initShellTemplate(ProceduralArenaType proceduralArenaType) {

        var shellId = new Identifier(LasertagMod.ID, ArenaType.PROCEDURAL.nbtFileId.getPath() + "/" + proceduralArenaType.mapFolderName + "/shell.litematic");
        var shellResource = ResourceManagers.STRUCTURE_RESOURCE_MANAGER.get(shellId);

        if (shellResource == null) {
            throw new RuntimeException("Could not find shell resource '" + shellId.getPath() + "' for arena '" + proceduralArenaType.translatableName + "'");
        }

        this.shellTemplate = initTemplate(shellId, shellResource, BlockPos.ORIGIN.subtract(arenaStartPos.add(-1, -1, -1)));
    }

    private StructureTemplate initTemplate(Identifier resourceId, Resource resource, Vec3i offset) {

        // Read nbt file
        NbtCompound nbt;
        try {
            nbt = NbtIo.readCompressed(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load nbt file for template '" + resourceId.getPath() + "'");
        }

        // If is litematic file
        if (resourceId.getPath().endsWith(StructureResourceManager.LITEMATIC_FILE_ENDING)) {

            // Convert litematic nbt compound to nbt nbt compound
            nbt = NbtUtil.convertLitematicToNbt(nbt, "main", offset);

            // Sanity check
            if (nbt == null) {
                throw new RuntimeException("Litematica file '" + resourceId + "' could not be converted to nbt.");
            }
        }

        var segmentTemplate = new StructureTemplate();
        segmentTemplate.readNbt(nbt);
        return segmentTemplate;
    }

    /**
     * Calculates the offset of the first chunk of the given segmentId inside the arena.
     *
     * @param proceduralArenaType
     * @param segmentId
     * @return The offset in blocks
     */
    private BlockPos getLocalChunkOffset(ProceduralArenaType proceduralArenaType, int segmentId) {
        int localChunkStartX = 0;
        int localChunkStartZ;
        for (var row : proceduralArenaType.segments) {
            localChunkStartZ = 0;

            for (var chunk : row) {
                if (chunk == segmentId) {
                    return new BlockPos(localChunkStartX * BLOCKS_PER_CHUNK, 0, localChunkStartZ * BLOCKS_PER_CHUNK);
                }

                ++localChunkStartZ;
            }

            ++localChunkStartX;
        }

        return null;
    }

    private boolean isInside(int x, int z, int minX, int minZ, int maxX, int maxZ) {
        var xInside = x >= minX && x <= maxX;
        var zInside = z >= minZ && z <= maxZ;

        return xInside && zInside;
    }
}
