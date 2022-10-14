package de.kleiner3.lasertag.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.kleiner3.lasertag.item.LasertagVestItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Mixin into class LivingEntity to get the trigger to join / leave teams
 * 
 * @author Étienne Muser
 *
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method="onEquipStack", at=@At("HEAD"))
	private void onPlayerEquipStack(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
		
		// If this is not a player
		if (!(((LivingEntity)(Object)this) instanceof PlayerEntity)) {
			return;
		}
		
		// If old item is lasertag vest
		Item oldItem = oldStack.getItem();
		if (oldItem instanceof LasertagVestItem) {
			// Cast to lasertag vest
			LasertagVestItem vest = (LasertagVestItem)oldItem;
			
			// Call leave team on vest
			vest.leaveTeam(((PlayerEntity)(Object)this));
		}
		
		// If new item is lasertag vest
		Item newItem = newStack.getItem();
		if (newItem instanceof LasertagVestItem) {
			// Cast to lasertag vest
			LasertagVestItem vest = (LasertagVestItem)newItem;
			
			// Call join team on vest
			vest.joinTeam(((PlayerEntity)(Object)this));
		}
	}
}
