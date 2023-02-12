package de.kleiner3.lasertag.types;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * Interface for Items that are assigned to a lasertag team
 *
 * @author Étienne Muser
 */
public interface ILasertagColorable {
    /**
     * Set the color via nbt data
     * @param stack The ItemStack to set the color of
     * @param color The color to set it to
     */
    default void setColor(ItemStack stack, int color) {
        var nbt = stack.getNbt();
        if (nbt == null) {
            nbt = new NbtCompound();
        }
        nbt.putInt("color", color);
        stack.setNbt(nbt);
    }

    /**
     * Get the color of the ItemStack
     * @param stack The item stack to get the color of
     * @return The int value of the color
     */
    default int getColor(ItemStack stack) {
        if (!stack.hasNbt()) {
            return 0xFFFFFF;
        }

        NbtCompound nbt = stack.getNbt();

        return nbt.getInt("color");
    }
}
