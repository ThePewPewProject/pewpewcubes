package de.kleiner3.lasertag.lasertaggame;

import java.util.LinkedList;
import java.util.List;

import de.kleiner3.lasertag.types.Colors;
import net.minecraft.entity.player.PlayerEntity;

public interface ILasertagGame {
	
	default public void startGame() {}
	
	default public List<PlayerEntity> getPlayersOfTeam(Colors color) { return new LinkedList<PlayerEntity>(); }
	
	default public void playerJoinTeam(Colors color, PlayerEntity player) {}
	
	default public void playerLeaveTeam(Colors color, PlayerEntity player) {}
	
	default public void onPlayerScored(PlayerEntity player, int score) {}
}
