package de.pewpewproject.lasertag.block.entity.render;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.block.entity.LaserTargetBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * Model for the lasertarget lights
 *
 * @author Étienne Muser
 */
public class LasertargetLightsModel extends AnimatedGeoModel<LaserTargetBlockEntity> {
    @Override
    public Identifier getModelResource(LaserTargetBlockEntity object) {
        return new Identifier(LasertagMod.ID, "geo/lasertarget_lights.geo.json");
    }

    @Override
    public Identifier getTextureResource(LaserTargetBlockEntity object) {
        return new Identifier(LasertagMod.ID, "textures/block/lasertag_target_light.png");
    }

    @Override
    public Identifier getAnimationResource(LaserTargetBlockEntity animatable) {
        return new Identifier(LasertagMod.ID, "animations/idle_animation.json");
    }
}
