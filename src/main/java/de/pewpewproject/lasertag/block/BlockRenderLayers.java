package de.pewpewproject.lasertag.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

/**
 * Sets the render layers for specific blocks
 *
 * @author Étienne Muser
 */
public class BlockRenderLayers {
    public static void register() {
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.LASER_TARGET, RenderLayer.getTranslucent());
    }
}
