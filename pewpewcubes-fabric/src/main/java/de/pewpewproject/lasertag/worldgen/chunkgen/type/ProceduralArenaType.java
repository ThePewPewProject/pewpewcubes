package de.pewpewproject.lasertag.worldgen.chunkgen.type;

import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;

/**
 * Enum to represent all possible procedural arenas
 *
 * @author Étienne Muser
 */
public enum ProceduralArenaType {

    SMALL_2V2("small_two_team",
            "arenaType.procedural.small_two_team",
            new int[][]
                    {
                            {1, 0, 1},
                            {1, 1, 1},
                            {2, 2, 2},
                            {2, 2, 2},
                            {3, 3, 3},
                            {3, 4, 3},
                    },
            new String[]
                    {
                            "teal_spawns",
                            "teal_segments",
                            "middle_segments",
                            "orange_segments",
                            "orange_spawns",
                    },
            new Vec3i(7, 0, 23)),

    LARGE_4V4("large_four_team",
            "arenaType.procedural.large_four_team",
            new int[][]
                    {
                            {0,  4,  4,  4,  4, 1},
                            {5,  8,  8,  9,  9, 6},
                            {5,  8,  8,  9,  9, 6},
                            {5, 10, 10, 11, 11, 6},
                            {5, 10, 10, 11, 11, 6},
                            {2,  7,  7,  7,  7, 3},
                    },
            new String[]
                    {
                            "green_spawns",
                            "blue_spawns",
                            "pink_spawns",
                            "red_spawns",
                            "east_segments",
                            "north_segments",
                            "south_segments",
                            "west_segments",
                            "first_quadrants",
                            "fourth_quadrants",
                            "second_quadrants",
                            "third_quadrants"
                    },
            new Vec3i(17, 0, 17));

    /**
     * Enum constructor
     *
     * @param mapFolderName The name of the folder containing the arena segment, lobby and shell nbt files
     * @param translatableName The name used for translation
     * @param segments Defines the arena segments. Indices with the same value belong to the same segment.
     * @param folderNameMapping Maps the values of <code>segments</code> to a folder name. This allows for multiple
     *                          segments to have the same nbt files.
     * @param lobbyOffset The offset with which to place the lobby. Insert the coordinates of the world spawn point block so that this block is 0, 0, 0
     *                        (To calculate: Generate arena with offset 0,0,0 and use the targeted block coordinates of the desired spawn point block as the offset)
     */
    ProceduralArenaType(String mapFolderName,
                        String translatableName,
                        int[][] segments,
                        String[] folderNameMapping,
                        Vec3i lobbyOffset) {
        this.mapFolderName = mapFolderName;
        this.translatableName = translatableName;
        this.segments = segments;
        this.folderNameMapping = folderNameMapping;
        this.lobbyOffset = lobbyOffset;
    }

    public final String mapFolderName;
    public final String translatableName;
    public final int[][] segments;
    public final String[] folderNameMapping;
    public final Vec3i lobbyOffset;

    private ArrayList<ArrayList<StructureTemplate>> templates = null;
}
