package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class LasertagItemGroupBuilder {
	public static ItemGroup build() {
		Identifier identifier = new Identifier("lasertag", "lasertag_item_group");
		((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
		return new ItemGroup(ItemGroup.GROUPS.length - 1, String.format("%s.%s", identifier.getNamespace(), identifier.getPath())) {
			@Override
			public ItemStack createIcon() {
				return new ItemStack(LasertagMod.LASERTAG_WEAPONS.get(0));
			}
		};
	}
}
