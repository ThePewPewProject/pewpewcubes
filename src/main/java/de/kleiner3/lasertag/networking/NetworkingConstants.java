package de.kleiner3.lasertag.networking;

import net.minecraft.util.Identifier;

/**
 * Class containing all networking constants
 * 
 * @author Étienne Muser
 *
 */
public class NetworkingConstants {
	// ===== Entities ===============
	public static final Identifier LASER_RAY_SPAWNED = new Identifier("lasertag", "laser_ray_spawned");
	
	// ===== Lasertag game ==========
	public static final Identifier LASERTAG_GAME_TEAMS_UPDATE = new Identifier("lasertag", "lasertag_game_teams_update");
	public static final Identifier LASERTAG_GAME_SCORE_UPDATE = new Identifier("lasertag", "lasertag_game_score_update");
	
	// ===== General ================
	public static final Identifier ERROR_MESSAGE = new Identifier("lasertag", "error_message");
}
