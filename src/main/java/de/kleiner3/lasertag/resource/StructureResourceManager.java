package de.kleiner3.lasertag.resource;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.StringUtil;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all .nbt file resources
 *
 * @author Étienne Muser
 */
public class StructureResourceManager {
    //region Private fields

    private final Map<Identifier, Resource> structureResources = new HashMap<>();

    public static final String LITEMATIC_FILE_ENDING = ".litematic";
    public static final String NBT_FILE_ENDING = ".nbt";
    public static final String[] FILE_ENDINGS = new String[]{NBT_FILE_ENDING, LITEMATIC_FILE_ENDING};

    //endregion

    public Resource get(Identifier id) {
        return structureResources.get(id);
    }

    public List<Map.Entry<Identifier, Resource>> getFolder(Identifier folderId) {
        return structureResources.entrySet().stream()
                .filter(entry -> entry.getKey().getPath().startsWith(folderId.getPath()))
                .toList();
    }

    public void reload() throws IOException, URISyntaxException {

        var uri = getClass().getClassLoader().getResource("data/lasertag/structures").toURI();

        structureResources.clear();
        try (var stream = Files.walk(Path.of(uri))) {

            stream.filter(Files::isRegularFile)
                    .filter(path -> StringUtil.stringEndsWithList(path.toString(), FILE_ENDINGS))
                    .forEach(path -> {

                        // Split the path by "structures"
                        var splitPath = path.toString().split("structures");

                        // Get the resource identifier id path
                        var resourceIdPath = ("structures" + splitPath[splitPath.length - 1]).replace(File.separatorChar, '/');

                        structureResources.put(
                                new Identifier(LasertagMod.ID, resourceIdPath),
                                new Resource(LasertagMod.ID, () -> Files.newInputStream(path)));
                    });
        }
    }
}
