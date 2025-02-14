package de.pewpewproject.lasertag.entity.render;

import de.pewpewproject.lasertag.entity.Entities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/**
 * Class for registering all entity renderers
 *
 * @author Étienne Muser
 */
public class EntityRenderers {
    public static void register() {
        EntityRendererRegistry.register(Entities.LASER_RAY, LaserRayEntityRenderer::new);
    }
}
