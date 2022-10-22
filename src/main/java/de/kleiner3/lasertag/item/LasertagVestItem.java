package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.types.ILasertagColorable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;

/**
 * Class to implement the custom behavior of the lasertag vest
 *
 * @author Étienne Muser
 */
public class LasertagVestItem extends DyeableArmorItem implements ILasertagColorable {
    public LasertagVestItem(ArmorMaterial armorMaterial, Settings settings) {
        super(armorMaterial, EquipmentSlot.CHEST, settings);
    }

    @Override
    public int getColor(ItemStack stack) {
        return super.getColor(stack);
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        super.setColor(stack, color);
    }
}
