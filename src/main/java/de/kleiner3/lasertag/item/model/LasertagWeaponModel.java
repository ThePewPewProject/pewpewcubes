package de.kleiner3.lasertag.item.model;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.item.LasertagWeaponItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * Model for the lasertag weapon
 *
 * @author Étienne Muser
 */
public class LasertagWeaponModel extends AnimatedGeoModel<LasertagWeaponItem> {
    @Override
    public Identifier getModelResource(LasertagWeaponItem object) {
        return new Identifier(LasertagMod.ID, "geo/lasertag_weapon.geo.json");
    }

    @Override
    public Identifier getTextureResource(LasertagWeaponItem object) {
        return new Identifier(LasertagMod.ID, "textures/item/lasertag_weapon.png");
    }

    @Override
    public Identifier getAnimationResource(LasertagWeaponItem animatable) {
        return new Identifier(LasertagMod.ID, "animations/idle_animation.json");
    }
}
