package de.pewpewproject.lasertag.item.model;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.item.LasertagFlagItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * Model for the lasertag flag lights
 *
 * @author Étienne Muser
 */
public class LasertagFlagLightsItemModel extends AnimatedGeoModel<LasertagFlagItem> {
    @Override
    public Identifier getModelResource(LasertagFlagItem object) {
        return new Identifier(LasertagMod.ID, "geo/lasertag_flag_lights.geo.json");
    }

    @Override
    public Identifier getTextureResource(LasertagFlagItem object) {
        return new Identifier(LasertagMod.ID, "textures/block/lasertag_flag.png");
    }

    @Override
    public Identifier getAnimationResource(LasertagFlagItem animatable) {
        return new Identifier(LasertagMod.ID, "animations/idle_animation.json");
    }
}
