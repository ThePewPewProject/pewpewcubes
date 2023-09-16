package de.kleiner3.lasertag.resource;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.common.util.StringUtil;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all local web site resources
 *
 * @author Étienne Muser
 */
public class WebResourceManager implements SimpleSynchronousResourceReloadListener {
    private static final String[] fileEndings = new String[] { ".html", ".js", ".css", ".png", ".svg", ".jgp", "htm", ".ts", ".ico"};

    private static final Map<Identifier, List<Tuple<Identifier, Resource>>> resourcesMap = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(LasertagMod.ID, "lasertag_web_resource_manager");
    }

    @Override
    public void reload(ResourceManager manager) {
        var resources = manager.findResources("web", path -> StringUtil.stringEndsWithList(path.getPath(), fileEndings));

        for (var resourceTuple : resources.entrySet()) {
            // Get key
            var key = resourceTuple.getKey();

            // Get path
            var path = key.getPath();

            // Split
            var split = StringUtil.splitAtNthChar(path, '/', 2);

            // Create map key
            var resourceKey = new Identifier(split[0]);

            // Create sub path
            var subPath = new Identifier(split[1]);

            // Create new list if not exist
            if (!resourcesMap.containsKey(resourceKey)) {
                resourcesMap.put(resourceKey, new ArrayList<>());
            }

            // Get web site
            var webSite = resourcesMap.get(resourceKey);

            webSite.add(new Tuple<>(subPath, resourceTuple.getValue()));
        }
    }

    public List<Tuple<Identifier, Resource>> getWebSite(Identifier id) {
        return resourcesMap.get(id);
    }
}
