package de.kleiner3.lasertag.worldgen.structure;

import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class StructureResourceManager {
    private Map<Identifier, Resource> structureResources = new HashMap<>();

    public void put(Identifier id, Resource resource) {
        structureResources.put(id, resource);
    }

    public Resource get(Identifier id) {
        return structureResources.get(id);
    }
}
