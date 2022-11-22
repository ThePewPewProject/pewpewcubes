package de.kleiner3.lasertag.resource;

import de.kleiner3.lasertag.LasertagMod;
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
    private Map<Identifier, Resource> structureResources = new HashMap<>();

    public Resource get(Identifier id) {
        return structureResources.get(id);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(LasertagMod.ID, "lasertag_structure_resource_manager");
    }

    @Override
    public void reload(ResourceManager manager) {
        var resources = manager.findResources("structures", path -> path.getPath().endsWith(".nbt"));
        for(var entry : resources.entrySet()) {
            if (entry.getKey().getNamespace().equals(LasertagMod.ID) == false) {
                continue;
            }

            structureResources.put(entry.getKey(), entry.getValue());
        }
    }
}
