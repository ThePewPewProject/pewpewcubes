package de.kleiner3.lasertag.resource;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

/**
 * Class for registering all resource managers
 *
 * @author Étienne Muser
 */
public class ResourceManagers {
    public static final StructureResourceManager STRUCTURE_RESOURCE_MANAGER = new StructureResourceManager();

    public static final WebResourceManager WEB_RESOURCE_MANAGER = new WebResourceManager();

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(STRUCTURE_RESOURCE_MANAGER);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(WEB_RESOURCE_MANAGER);
    }
}
