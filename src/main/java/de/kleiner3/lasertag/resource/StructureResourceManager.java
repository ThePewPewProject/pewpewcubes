package de.kleiner3.lasertag.resource;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.StringUtil;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all .nbt file resources
 *
 * @author Étienne Muser
 */
public class StructureResourceManager implements SimpleSynchronousResourceReloadListener {
    private final Map<Identifier, Resource> structureResources = new HashMap<>();

    public Resource get(Identifier id) {
        return structureResources.get(id);
    }

    public static final String LITEMATIC_FILE_ENDING = ".litematic";
    public static final String NBT_FILE_ENDING = ".nbt";
    public static final String[] FILE_ENDINGS = new String[] { NBT_FILE_ENDING, LITEMATIC_FILE_ENDING };

    @Override
    public Identifier getFabricId() {
        return new Identifier(LasertagMod.ID, "lasertag_structure_resource_manager");
    }

    @Override
    public void reload(ResourceManager manager) {
        var resources = manager.findResources("structures", path -> StringUtil.stringEndsWithList(path.getPath(), FILE_ENDINGS));
        for(var entry : resources.entrySet()) {
            if (!entry.getKey().getNamespace().equals(LasertagMod.ID)) {
                continue;
            }

            structureResources.put(entry.getKey(), entry.getValue());
        }
    }
}
