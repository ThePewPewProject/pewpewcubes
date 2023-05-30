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
    public static final Identifier SCORE_UPDATE = new Identifier(LasertagMod.ID, "score_update");
    public static final Identifier TEAM_UPDATE = new Identifier(LasertagMod.ID, "team_update");
    public static final Identifier SETTINGS_CHANGED = new Identifier(LasertagMod.ID, "settings_changed");
    public static final Identifier GAME_MANAGER_SYNC = new Identifier(LasertagMod.ID, "game_manager_sync");
    public static final Identifier PLAYER_DEACTIVATED_STATUS_CHANGED = new Identifier(LasertagMod.ID, "player_deactivated_status_changed");
    public static final Identifier GAME_STATISTICS = new Identifier(LasertagMod.ID, "game_statistics");


    // ===== General ================
    public static final Identifier ERROR_MESSAGE = new Identifier(LasertagMod.ID, "error_message");

    // ===== Events =================
    public static final Identifier PLAY_WEAPON_FIRED_SOUND = new Identifier(LasertagMod.ID, "play_weapon_fired_sound");
    public static final Identifier PLAY_WEAPON_FAILED_SOUND = new Identifier(LasertagMod.ID, "play_weapon_failed_sound");
    public static final Identifier PLAY_PLAYER_SCORED_SOUND = new Identifier(LasertagMod.ID, "play_player_scored_sound");
    public static final Identifier PLAY_PLAYER_DEACTIVATED_SOUND = new Identifier(LasertagMod.ID, "play_player_deactivated_sound");
    public static final Identifier PLAY_PLAYER_ACTIVATED_SOUND = new Identifier(LasertagMod.ID, "play_player_activated_sound");
    public static final Identifier GAME_STARTED = new Identifier(LasertagMod.ID, "game_started");
    public static final Identifier GAME_START_ABORTED = new Identifier(LasertagMod.ID, "game_start_aborted");
    public static final Identifier GAME_OVER = new Identifier(LasertagMod.ID, "game_over");
    public static final Identifier PROGRESS = new Identifier(LasertagMod.ID, "progress");
    public static final Identifier SCORE_RESET = new Identifier(LasertagMod.ID, "score_reset");
}
