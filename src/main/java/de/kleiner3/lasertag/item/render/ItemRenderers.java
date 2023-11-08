package de.kleiner3.lasertag.item.render;

import de.kleiner3.lasertag.item.Items;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

/**
 * Class for registering all item renderers
 *
 * @author Étienne Muser
 */
public class ItemRenderers {
    public static void register() {
        GeoItemRenderer.registerItemRenderer(Items.LASERTAG_WEAPON, new LasertagWeaponRenderer());
        GeoItemRenderer.registerItemRenderer(Items.LASERTAG_FLAG, new LasertagFlagItemRenderer());
    }
}
