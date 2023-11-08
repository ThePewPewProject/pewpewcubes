package de.kleiner3.lasertag.item.model;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.item.LasertagFlagItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * Model for the lasertag flag
 *
 * @author Étienne Muser
 */
public class LasertagFlagItemModel extends AnimatedGeoModel<LasertagFlagItem> {

    @Override
    public Identifier getModelResource(LasertagFlagItem object) {
        return new Identifier(LasertagMod.ID, "geo/lasertag_flag.geo.json");
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
