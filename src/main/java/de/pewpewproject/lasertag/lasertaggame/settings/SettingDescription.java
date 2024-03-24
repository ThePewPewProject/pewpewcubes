package de.pewpewproject.lasertag.lasertaggame.settings;

import de.pewpewproject.lasertag.lasertaggame.settings.valuetypes.CTFFlagHoldingPlayerVisibility;

import java.util.Arrays;
import java.util.Optional;

/**
 * DTO holding all information about a setting
 *
 * @author Étienne Muser
 */
public enum SettingDescription {

    // General
    WEAPON_COOLDOWN("weaponCooldown", 4L, SettingDataType.LONG, "ticks", 0L, null),
    WEAPON_REACH("weaponReach", 200L, SettingDataType.LONG, "blocks", 0L, null),
    SHOW_LASER_RAYS("showLaserRays", true, SettingDataType.BOOL, "value", null, null),
    MAX_TEAM_SIZE("maxTeamSize", 6L, SettingDataType.LONG, "players", 0L, null),
    RENDER_TEAM_LIST("renderTeamList", true, SettingDataType.BOOL, "value", null, null),
    RENDER_TIMER("renderTimer", true, SettingDataType.BOOL, "value", null, null),
    PREGAME_DURATION("pregameDuration", 10L, SettingDataType.LONG, "seconds", 0L, null),
    PLAYER_DEACTIVATE_TIME("playerDeactivationDuration", 5L, SettingDataType.LONG, "seconds", 0L, null),
    LASERTARGET_DEACTIVATE_TIME("lasertargetDeactivatedDuration", 7L, SettingDataType.LONG, "seconds", 0L, null),
    GEN_STATS_FILE("generateStatsFile", true, SettingDataType.BOOL, "value", null, null),
    AUTO_OPEN_STATS_FILE("autoOpenStatsFile", false, SettingDataType.BOOL, "value", null, null),
    DO_ORIGIN_SPAWN("doOriginSpawn", true, SettingDataType.BOOL, "value", null, null),
    RESPAWN_PENALTY("respawnPenalty", 0L, SettingDataType.LONG, "seconds", 0L, null),
    SHOW_NAMETAGS_OF_TEAMMATES("showNametagsOfTeammates", true, SettingDataType.BOOL, "value", null, null),
    MINING_FATIGUE_ENABLED("miningFatigueEnabled", true, SettingDataType.BOOL, "value", null, null),
    WEAPON_ZOOM("weaponZoom", 2L, SettingDataType.LONG, "multiplier", 1L, null),
    RELOAD_ARENAS_BEFORE_GAME("reloadArenasBeforeGame", false, SettingDataType.BOOL, "value", null, null),
    FRIENDLY_FIRE_ENABLED("friendlyFireEnabled", false, SettingDataType.BOOL, "value", null, null),

    // Time limited specific
    PLAY_TIME("gameDuration", 10L, SettingDataType.LONG, "minutes", 1L, null),

    // Point based specific
    LASERTARGET_HIT_SCORE("lasertargetHitScore", 100L, SettingDataType.LONG, "points", null, null),
    PLAYER_HIT_SCORE("playerHitScore", 20L, SettingDataType.LONG, "points", null, null),
    DEATH_PENALTY("deathPenalty", 500L, SettingDataType.LONG, "points", null, null),

    // Damage based specific
    LASER_RAY_DAMAGE("laserDamage", 5L, SettingDataType.LONG, "amount", 0L, 20L),
    LASERTARGET_HEAL("lasertargetHeal", 5L, SettingDataType.LONG, "amount", 0L, 20L),
    PLAYER_RESET_HEAL("playerResetHeal", 20L, SettingDataType.LONG, "amount", 0L, 20L),

    // Capture the flag specific
    FLAG_COUNT("numberOfFlags", 3L, SettingDataType.LONG, "number", 0L, null),
    CTF_FLAG_HOLDING_PLAYER_VISIBILITY("flagHoldingPlayerVisibility", CTFFlagHoldingPlayerVisibility.GLOW, SettingDataType.ofEnum(CTFFlagHoldingPlayerVisibility.class), "visibility", null, null),
    SEND_FLAG_STOLEN_MESSAGE("sendFlagStolenMessage", true, SettingDataType.BOOL, "value", null, null),
    SEND_FLAG_CAPTURED_MESSAGE("sendFlagCapturedMessage", true, SettingDataType.BOOL, "value", null, null),
    SEND_TEAM_OUT_MESSAGE("sendTeamOutMessage", true, SettingDataType.BOOL, "value", null, null),

    // Musical chairs specific
    PHASE_DURATION("phaseDuration", 2L, SettingDataType.LONG, "minutes", 1L, null),
    RESET_SCORES_AT_PHASE_END("resetScoresAtPhaseEnd", false, SettingDataType.BOOL, "value", null, null),

    // Elimination specific
    INITIAL_BORDER_SIZE("initialBorderSize", 200L, SettingDataType.LONG, "blocks", 1L, null),
    BORDER_SHRINK_DISTANCE("borderShrinkDistance", 10L, SettingDataType.LONG, "blocks", 0L, null),
    BORDER_SHRINK_TIME("borderShrinkTime", 10L, SettingDataType.LONG, "seconds", 0L, 59L);

    /**
     * Get a SettingDescription given its name.
     *
     * @param settingName The configured name of the SettingDescription
     * @return Optional containing the SettingDescription or Optional.empty()
     */
    public static Optional<SettingDescription> byName(String settingName) {
        return Arrays.stream(SettingDescription.values())
                .filter(sd -> sd.name.equals(settingName))
                .findFirst();
    }

    private final String name;
    private final Object defaultValue;
    private final SettingDataType dataType;
    private final String settingValueName;
    private final Object minValue;
    private final Object maxValue;

    SettingDescription(String name,
                       Object defaultValue,
                       SettingDataType dataType,
                       String settingValueName,
                       Object minValue,
                       Object maxValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.dataType = dataType;
        this.settingValueName = settingValueName;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public SettingDataType getDataType() {
        return dataType;
    }

    public String getSettingValueName() {
        return settingValueName;
    }

    public Object getMinValue() {
        return minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }
}
