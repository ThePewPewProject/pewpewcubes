package de.kleiner3.lasertag.networking;

import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.util.Identifier;

/**
 * Class containing all networking constants
 *
 * @author Étienne Muser
 */
public class NetworkingConstants {
    // ===== Entities ===============
    public static final Identifier LASER_RAY_SPAWNED = new Identifier(LasertagMod.ID, "laser_ray_spawned");

    // ===== Lasertag game ==========
    public static final Identifier LASERTAG_GAME_TEAM_OR_SCORE_UPDATE = new Identifier(LasertagMod.ID, "lasertag_game_team_or_score_update");

    // ===== General ================
    public static final Identifier ERROR_MESSAGE = new Identifier(LasertagMod.ID, "error_message");
}
