package de.pewpewproject.lasertag.block.entity;

import de.pewpewproject.lasertag.block.entity.render.LasertagFlagRenderer;
import de.pewpewproject.lasertag.block.entity.render.LasertagStartGameButtonBlockEntityRenderer;
import de.pewpewproject.lasertag.block.entity.render.LasertagTeamZoneGeneratorBlockEntityRenderer;
import de.pewpewproject.lasertag.block.entity.render.LasertargetRenderer;
import de.pewpewproject.lasertag.entity.Entities;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

/**
 * Class for registering all entity renderers
 *
 * @author Étienne Muser
 */
public class BlockEntityRenderers {
    public static void register() {
        BlockEntityRendererRegistry.register(Entities.LASER_TARGET_ENTITY, LasertargetRenderer::new);
        BlockEntityRendererRegistry.register(Entities.LASERTAG_START_GAME_BUTTON_ENTITY, LasertagStartGameButtonBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(Entities.LASERTAG_TEAM_ZONE_GENERATOR_BLOCK_ENTITY, LasertagTeamZoneGeneratorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(Entities.LASERTAG_FLAG_BLOCK_ENTITY, LasertagFlagRenderer::new);
    }
}
