package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * Helper class to build the lasertag item group
 *
 * @author Étienne Muser
 */
public class LasertagItemGroupBuilder {

    /**
     * Build the lasertag item group with the lasertag weapon as icon
     *
     * @return The lasertag item group
     */
    public static ItemGroup build() {
        Identifier identifier = new Identifier(LasertagMod.ID, "lasertag_item_group");
        ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
        return new ItemGroup(ItemGroup.GROUPS.length - 1, String.format("%s.%s", identifier.getNamespace(), identifier.getPath())) {
            @Override
            public ItemStack createIcon() {
                var stack = new ItemStack(LasertagMod.LASERTAG_WEAPON);
                var nbt = new NbtCompound();
                nbt.putInt("color", 0xFF0000);
                stack.setNbt(nbt);
                return stack;
            }
        };
    }
}
