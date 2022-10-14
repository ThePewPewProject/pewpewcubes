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
 *
 */
public class LasertagVestItem extends DyeableArmorItem{

	private Colors color;
	
	public LasertagVestItem(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Settings settings, Colors color) {
		super(armorMaterial, equipmentSlot, settings);

		this.color = color;
	}

	public Colors getColor() {
		return color;
	}
	
	public void joinTeam(PlayerEntity user) {
		MinecraftServer server = user.getServer();
		
		// If we are on the server
		if (server != null) {
			server.playerJoinTeam(color, user);
		}
	}
	
	public void leaveTeam(PlayerEntity user) {
		MinecraftServer server = user.getServer();
		
		// If we are on the server
		if (server != null) {
			server.playerLeaveTeam(color, user);
		}
	}
}
