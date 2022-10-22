package de.kleiner3.lasertag.types;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface ILasertagColorable {
    default public void setColor(ItemStack stack, int color) {
        var nbt = new NbtCompound();
        nbt.putInt("color", color);
        stack.setNbt(nbt);
    }

    default public int getColor(ItemStack stack) {
        if (!stack.hasNbt()) {
            return 0xFFFFFF;
        }

        NbtCompound nbt = stack.getNbt();

        return nbt.getInt("color");
    }
}
