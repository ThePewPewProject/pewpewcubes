package de.pewpewproject.lasertag.entity.render.armor;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.item.LasertagVestItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * Model for the lasertag vest
 *
 * @author Étienne Muser
 */
public class LasertagVestModel extends AnimatedGeoModel<LasertagVestItem> {
    @Override
    public Identifier getModelResource(LasertagVestItem object) {
        return new Identifier(LasertagMod.ID, "geo/lasertag_vest.geo.json");
    }

    @Override
    public Identifier getTextureResource(LasertagVestItem object) {
        return new Identifier(LasertagMod.ID, "textures/armor/lasertag_vest.png");
    }

    @Override
    public Identifier getAnimationResource(LasertagVestItem animatable) {
        return new Identifier(LasertagMod.ID, "animations/idle_animation.json");
    }
}
