package de.pewpewproject.lasertag.block.entity.render;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.block.entity.LasertagFlagBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * Model for the lasertag flag lights
 *
 * @author Étienne Muser
 */
public class LasertagFlagLightsModel extends AnimatedGeoModel<LasertagFlagBlockEntity> {
    @Override
    public Identifier getModelResource(LasertagFlagBlockEntity object) {
        return new Identifier(LasertagMod.ID, "geo/lasertag_flag_lights.geo.json");
    }

    @Override
    public Identifier getTextureResource(LasertagFlagBlockEntity object) {
        return new Identifier(LasertagMod.ID, "textures/block/lasertag_flag.png");
    }

    @Override
    public Identifier getAnimationResource(LasertagFlagBlockEntity animatable) {
        return new Identifier(LasertagMod.ID, "animations/idle_animation.json");
    }
}
