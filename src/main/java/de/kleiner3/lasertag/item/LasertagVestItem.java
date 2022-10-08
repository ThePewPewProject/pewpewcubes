package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.Types.Colors;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;

public class LasertagVestItem extends DyeableArmorItem{

	private Colors color;
	
	public LasertagVestItem(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Settings settings, Colors color) {
		super(armorMaterial, equipmentSlot, settings);

		this.color = color;
	}

	public Colors getColor() {
		return color;
	}
}
