package de.kleiner3.lasertag.item;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;

public class LasertagWeaponItem extends RangedWeaponItem {

	public LasertagWeaponItem(Settings settings) {
		super(settings);
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		// Laser has no projectiles
		return null;
	}

	@Override
	public int getRange() {
		// Laser has infinite range
		return Integer.MAX_VALUE;
	}

}
