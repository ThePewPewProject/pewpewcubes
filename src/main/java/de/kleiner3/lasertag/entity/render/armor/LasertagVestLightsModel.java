package de.kleiner3.lasertag.entity.render.armor;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.item.LasertagVestItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LasertagVestLightsModel extends AnimatedGeoModel<LasertagVestItem> {
    @Override
    public Identifier getModelResource(LasertagVestItem object) {
        return new Identifier(LasertagMod.ID, "geo/lasertag_vest_lights.geo.json");
    }

    @Override
    public Identifier getTextureResource(LasertagVestItem object) {
        return new Identifier(LasertagMod.ID, "textures/armor/lasertag_vest_lights.png");
    }

    @Override
    public Identifier getAnimationResource(LasertagVestItem animatable) {
        return new Identifier(LasertagMod.ID, "animations/idle_animation.json");
    }
}
