package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.types.ILasertagColorable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to implement the custom behavior of the lasertag vest
 *
 * @author Étienne Muser
 */
public class LasertagVestItem extends ArmorItem implements ILasertagColorable, IAnimatable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public LasertagVestItem(ArmorMaterial armorMaterial, Settings settings) {
        super(armorMaterial, EquipmentSlot.CHEST, settings);
    }

    // Predicate runs every frame
    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        // This is all the extradata this event carries. The livingentity is the entity
        // that's wearing the armor. The itemstack and equipmentslottype are self
        // explanatory.
        LivingEntity livingEntity = event.getExtraDataOfType(LivingEntity.class).get(0);

        // Always loop the animation but later on in this method we'll decide whether or
        // not to actually play it
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));

        // If the living entity is an armorstand just play the animation nonstop
        if (livingEntity instanceof ArmorStandEntity) {
            return PlayState.CONTINUE;
        }

        // elements 2 to 6 are the armor so we take the sublist. Armorlist now only
        // contains the 4 armor slots
        List<Item> armorList = new ArrayList<>(4);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                if (livingEntity.getEquippedStack(slot) != null) {
                    armorList.add(livingEntity.getEquippedStack(slot).getItem());
                }
            }
        }

        // Make sure the player is wearing all the armor. If they are, continue playing
        // the animation, otherwise stop
        boolean isWearingAll = armorList.contains(Items.LASERTAG_VEST);
        return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
    }

    // All you need to do here is add your animation controllers to the
    // AnimationData
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<LasertagVestItem>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
