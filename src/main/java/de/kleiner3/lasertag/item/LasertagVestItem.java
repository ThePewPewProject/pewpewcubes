package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.types.Colors;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.server.MinecraftServer;

/**
 * Class to implement the custom behavior of the lasertag vest
 *
 * @author Étienne Muser
 */
public class LasertagVestItem extends DyeableArmorItem {

    /**
     * The color of the vest
     */
    private Colors color;

    public LasertagVestItem(ArmorMaterial armorMaterial, Settings settings, Colors color) {
        super(armorMaterial, EquipmentSlot.CHEST, settings);

        this.color = color;
    }

    public Colors getColor() {
        return color;
    }

    /**
     * Adds the player to the team of this vests color
     *
     * @param user
     */
    public void joinTeam(PlayerEntity user) {
        MinecraftServer server = user.getServer();

        // If we are on the server
        if (server != null) {
            server.playerJoinTeam(color, user);
        }
    }

    /**
     * Removes the player from the team of this vests color
     *
     * @param user
     */
    public void leaveTeam(PlayerEntity user) {
        MinecraftServer server = user.getServer();

        // If we are on the server
        if (server != null) {
            server.playerLeaveTeam(color, user);
        }
    }
}
