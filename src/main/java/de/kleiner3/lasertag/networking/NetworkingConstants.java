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
    public static final Identifier SETTING_CHANGED = new Identifier(LasertagMod.ID, "setting_changed");
    public static final Identifier SETTINGS_CHANGED = new Identifier(LasertagMod.ID, "settings_changed");
    public static final Identifier GAME_MANAGER_SYNC = new Identifier(LasertagMod.ID, "game_manager_sync");
    public static final Identifier PLAYER_DEACTIVATED_STATUS_CHANGED = new Identifier(LasertagMod.ID, "player_deactivated_status_changed");
    public static final Identifier GAME_STATISTICS = new Identifier(LasertagMod.ID, "game_statistics");
    public static final Identifier MAP_LOADING_EVENT = new Identifier(LasertagMod.ID, "map_loading");


    // ===== General ================
    public static final Identifier ERROR_MESSAGE = new Identifier(LasertagMod.ID, "error_message");

    // ===== Events =================
    public static final Identifier PLAY_PLAYER_SCORED_SOUND = new Identifier(LasertagMod.ID, "play_player_scored_sound");
    public static final Identifier PLAY_PLAYER_DEACTIVATED_SOUND = new Identifier(LasertagMod.ID, "play_player_deactivated_sound");
    public static final Identifier PLAY_PLAYER_ACTIVATED_SOUND = new Identifier(LasertagMod.ID, "play_player_activated_sound");
    public static final Identifier GAME_STARTED = new Identifier(LasertagMod.ID, "game_started");
    public static final Identifier GAME_START_ABORTED = new Identifier(LasertagMod.ID, "game_start_aborted");
    public static final Identifier GAME_OVER = new Identifier(LasertagMod.ID, "game_over");
    public static final Identifier PROGRESS = new Identifier(LasertagMod.ID, "progress");
    public static final Identifier SCORE_RESET = new Identifier(LasertagMod.ID, "score_reset");
    public static final Identifier PLAYER_HIT_LASERTARGET = new Identifier(LasertagMod.ID, "player_hit_lasertarget");
    public static final Identifier PLAYER_HIT_PLAYER = new Identifier(LasertagMod.ID, "player_hit_player");
    public static final Identifier PLAYER_JOINED = new Identifier(LasertagMod.ID, "player_joined");
    public static final Identifier SETTINGS_PRESET_ADDED = new Identifier(LasertagMod.ID, "settings_preset_added");
    public static final Identifier SETTINGS_PRESET_REMOVED = new Identifier(LasertagMod.ID, "settings_preset_removed");
    public static final Identifier TEAM_CONFIG_RELOADED = new Identifier(LasertagMod.ID, "team_config_reloaded");
    public static final Identifier GAME_MODE_SYNC = new Identifier(LasertagMod.ID, "game_mode_sync");
    public static final Identifier CLIENT_TRIGGER_RELOAD_TEAM_CONFIG = new Identifier(LasertagMod.ID, "client_trigger_reload_team_config");
    public static final Identifier CLIENT_TRIGGER_SETTING_RESET = new Identifier(LasertagMod.ID, "client_trigger_setting_reset");
    public static final Identifier CLIENT_TRIGGER_SETTINGS_RESET = new Identifier(LasertagMod.ID, "client_trigger_settings_reset");
    public static final Identifier CLIENT_TRIGGER_SETTING_CHANGE = new Identifier(LasertagMod.ID, "client_trigger_setting_change");
    public static final Identifier CLIENT_TRIGGER_SAVE_PRESET = new Identifier(LasertagMod.ID, "client_trigger_save_preset");
    public static final Identifier CLIENT_TRIGGER_LOAD_PRESET = new Identifier(LasertagMod.ID, "client_trigger_load_preset");
    public static final Identifier CLIENT_TRIGGER_DELETE_PRESET = new Identifier(LasertagMod.ID, "client_trigger_delete_preset");
    public static final Identifier CLIENT_TRIGGER_LOAD_MAP = new Identifier(LasertagMod.ID, "client_trigger_load_map");
    public static final Identifier CLIENT_TRIGGER_PLAYER_KICK = new Identifier(LasertagMod.ID, "client_trigger_player_kick");
    public static final Identifier CLIENT_TRIGGER_PLAYER_JOIN_TEAM = new Identifier(LasertagMod.ID, "client_trigger_player_join_team");
    public static final Identifier CLIENT_TRIGGER_GAME_MODE_CHANGE = new Identifier(LasertagMod.ID, "client_trigger_game_mode_change");
    public static final Identifier CLIENT_TRIGGER_GENERATE_ZONE = new Identifier(LasertagMod.ID, "set_team_zone_generator_block_team_name");
}
