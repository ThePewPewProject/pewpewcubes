package de.kleiner3.lasertag.resource;

import de.kleiner3.lasertag.LasertagMod;
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

    static {
        try {
            STRUCTURE_RESOURCE_MANAGER.reload();
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Could not load arena structure resources:", ex);
        }
    }

    public static void register() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(WEB_RESOURCE_MANAGER);
    }
}
