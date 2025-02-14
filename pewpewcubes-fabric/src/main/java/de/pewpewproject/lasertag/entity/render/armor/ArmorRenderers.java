package de.pewpewproject.lasertag.entity.render.armor;

import de.pewpewproject.lasertag.item.Items;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

/**
 * Class for registering all armor renderers
 *
 * @author Étienne Muser
 */
public class ArmorRenderers {
    public static void register() {
        GeoArmorRenderer.registerArmorRenderer(new LasertagVestRenderer(), Items.LASERTAG_VEST);
    }
}
