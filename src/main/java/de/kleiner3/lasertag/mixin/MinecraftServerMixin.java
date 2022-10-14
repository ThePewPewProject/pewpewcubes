package de.kleiner3.lasertag.mixin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.Gson;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.lasertaggame.ILasertagGame;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.types.Colors;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Interface injection into MinecraftServer to implement the lasertag game
 * 
 * @author Étienne Muser
 *
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ILasertagGame {

	private HashMap<Colors, List<PlayerEntity>> teamMap = new HashMap<>();
	
	/**
	 * Inject into constructor of MinecraftServer
	 * @param info
	 */
	@Inject(method="<init>", at=@At("TAIL"))
	private void init(CallbackInfo info) {
		
		// Initialize team map
		for (Colors color : Colors.values()) {
			teamMap.put(color, new LinkedList<>());
		}
	}
	
	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PlayerEntity> getPlayersOfTeam(Colors color) {
		return teamMap.get(color);
	}

	@Override
	public void playerJoinTeam(Colors newTeamColor, PlayerEntity player) {
		System.out.println("player " + player.getName().getString() + " tries to join team " + newTeamColor.name());
		
		// Check if team is full
		if (teamMap.get(newTeamColor).size() >= LasertagConfig.maxTeamSize)
		{
			// If is Server
			if(player instanceof ServerPlayerEntity) {
				ServerEventSending.sendErrorMessageToClient((ServerPlayerEntity)player, "Team " + newTeamColor.name() + " is full.");
			}
			return;
		}
		
		// Check if player is in a team already
		Colors oldTeamColor = null;
		for (Colors c : Colors.values()) {
			if (teamMap.get(c).contains(player)) {
				oldTeamColor = c;
				break;
			}
		}
		
		// If player has no team
		if (oldTeamColor == null) {
			teamMap.get(newTeamColor).add(player);
		} else {
			// If player tries to join his team again
			if (newTeamColor == oldTeamColor) return;
			
			teamMap.get(oldTeamColor).remove(player);
			teamMap.get(oldTeamColor).add(player);
		}
		
		// Notify about change
		notifyPlayersAboutTeamUpdate();
	}
	
	@Override
	public void playerLeaveTeam(Colors oldTeamColor, PlayerEntity player) {
		System.out.println("player " + player.getName().getString() + " tries to leave team " + oldTeamColor.name());
		
		// Get the players in the team
		List<PlayerEntity> team = teamMap.get(oldTeamColor);
		
		// Check if player is in the team he claims to be
		if (!team.contains(player)) {
			return;
		}
		
		// Notify about change
		notifyPlayersAboutTeamUpdate();
	}

	@Override
	public void onPlayerScored(PlayerEntity player, int score) {
		player.increaseScore(score);

		notifyPlayersAboutScoreUpdate();
	}
	
	private void notifyPlayersAboutTeamUpdate() {
		// Serialize team map to json
		String messagesString = new Gson().toJson(teamMap);
		
		// Create packet buffer
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		
		// Write team map string to buffer
		buf.writeString(messagesString);
		
		// Send to all clients
		ServerEventSending.sendToEveryone(((MinecraftServer)(Object)this).getOverworld(), NetworkingConstants.LASERTAG_GAME_TEAMS_UPDATE, buf);
	}
	
	private void notifyPlayersAboutScoreUpdate() {
		// Create the score map
		HashMap<Colors, LinkedList<Integer>> scoreMap = new HashMap<>();
		
		// Fill score map
		// For every possible color
		for (Colors color : Colors.values()) {
			// Create new list for the scores of this team
			LinkedList<Integer> scores = new LinkedList<>();
			
			// For each player in the team
			for (PlayerEntity player : teamMap.get(color)) {
				// Add the score to the scores list
				scores.add(player.getScore());
			}
			
			scoreMap.put(color, scores);
		}
		
		// Serialize score map to json
		String messagesString = new Gson().toJson(scoreMap);
		
		// Create packet buffer
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		
		// Write team map string to buffer
		buf.writeString(messagesString);
		
		// Send to all clients
		ServerEventSending.sendToEveryone(((MinecraftServer)(Object)this).getOverworld(), NetworkingConstants.LASERTAG_GAME_SCORE_UPDATE, buf);
	}
	
	private int getScoreOfTeam(Colors color) {
		int score = 0;
		
		for (PlayerEntity player : teamMap.get(color)) {
			score += player.getLasertagScore();
		}
		
		return score;
	}
}
