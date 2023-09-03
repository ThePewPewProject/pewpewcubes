package de.kleiner3.lasertag.block.entity.render;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * Model for the lasertarget
 *
 * @author Étienne Muser
 */
public class LasertargetModel extends AnimatedGeoModel<LaserTargetBlockEntity> {
    @Override
    public Identifier getModelResource(LaserTargetBlockEntity object) {
        return new Identifier(LasertagMod.ID, "geo/lasertarget.geo.json");
    }

    @Override
    public Identifier getTextureResource(LaserTargetBlockEntity object) {
        return new Identifier(LasertagMod.ID, "textures/block/lasertag_target_base.png");
    }

    @Override
    public Identifier getAnimationResource(LaserTargetBlockEntity animatable) {
        return new Identifier(LasertagMod.ID, "animations/idle_animation.json");
    }
}
