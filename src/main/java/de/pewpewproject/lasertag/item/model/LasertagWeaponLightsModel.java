package de.pewpewproject.lasertag.item.model;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.item.LasertagWeaponItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * Model for the lasertag weapon lights
 *
 * @author Étienne Muser
 */
public class LasertagWeaponLightsModel extends AnimatedGeoModel<LasertagWeaponItem> {
    @Override
    public Identifier getModelResource(LasertagWeaponItem object) {
        return new Identifier(LasertagMod.ID, "geo/lasertag_weapon_lights.geo.json");
    }

    @Override
    public Identifier getTextureResource(LasertagWeaponItem object) {
        return new Identifier(LasertagMod.ID, "textures/item/lasertag_weapon_lights.png");
    }

    @Override
    public Identifier getAnimationResource(LasertagWeaponItem animatable) {
        return new Identifier(LasertagMod.ID, "animations/idle_animation.json");
    }
}
