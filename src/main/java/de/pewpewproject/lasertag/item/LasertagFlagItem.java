package de.pewpewproject.lasertag.item;

import de.pewpewproject.lasertag.block.Blocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Class to implement the custom behavior of the lasertag flag
 *
 * @author Étienne Muser
 */
public class LasertagFlagItem extends BlockItem implements IAnimatable {

    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public LasertagFlagItem() {
        super(Blocks.LASERTAG_FLAG_BLOCK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP));
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    private PlayState predicate(AnimationEvent<LasertagFlagItem> event) {
        return PlayState.CONTINUE;
    }
}
