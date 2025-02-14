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
    WEAPON_COOLDOWN("weaponCooldown", 4L, SettingDataType.LONG, "ticks", 0L, null, false),
    WEAPON_REACH("weaponReach", 200L, SettingDataType.LONG, "blocks", 0L, null, false),
    SHOW_LASER_RAYS("showLaserRays", true, SettingDataType.BOOL, "value", null, null, false),
    MAX_TEAM_SIZE("maxTeamSize", 6L, SettingDataType.LONG, "players", 0L, null, false),
    RENDER_TEAM_LIST("renderTeamList", true, SettingDataType.BOOL, "value", null, null, false),
    RENDER_TIMER("renderTimer", true, SettingDataType.BOOL, "value", null, null, false),
    PREGAME_DURATION("pregameDuration", 10L, SettingDataType.LONG, "seconds", 0L, null, false),
    PLAYER_DEACTIVATE_TIME("playerDeactivationDuration", 5L, SettingDataType.LONG, "seconds", 0L, null, false),
    LASERTARGET_DEACTIVATE_TIME("lasertargetDeactivatedDuration", 7L, SettingDataType.LONG, "seconds", 0L, null, false),
    GEN_STATS_FILE("generateStatsFile", true, SettingDataType.BOOL, "value", null, null, true),
    AUTO_OPEN_STATS_FILE("autoOpenStatsFile", false, SettingDataType.BOOL, "value", null, null, true),
    DO_ORIGIN_SPAWN("doOriginSpawn", true, SettingDataType.BOOL, "value", null, null, true),
    RESPAWN_PENALTY("respawnPenalty", 0L, SettingDataType.LONG, "seconds", 0L, null, false),
    SHOW_NAMETAGS_OF_TEAMMATES("showNametagsOfTeammates", true, SettingDataType.BOOL, "value", null, null, false),
    MINING_FATIGUE_ENABLED("miningFatigueEnabled", true, SettingDataType.BOOL, "value", null, null, false),
    WEAPON_ZOOM("weaponZoom", 2L, SettingDataType.LONG, "multiplier", 1L, null, false),
    RELOAD_ARENAS_BEFORE_GAME("reloadArenasBeforeGame", false, SettingDataType.BOOL, "value", null, null, false),
    FRIENDLY_FIRE_ENABLED("friendlyFireEnabled", false, SettingDataType.BOOL, "value", null, null, false),

    // Time limited specific
    PLAY_TIME("gameDuration", 10L, SettingDataType.LONG, "minutes", 1L, null, false),

    // Point based specific
    LASERTARGET_HIT_SCORE("lasertargetHitScore", 100L, SettingDataType.LONG, "points", null, null, false),
    PLAYER_HIT_SCORE("playerHitScore", 20L, SettingDataType.LONG, "points", null, null, false),
    DEATH_PENALTY("deathPenalty", 500L, SettingDataType.LONG, "points", null, null, false),

    // Damage based specific
    LASER_RAY_DAMAGE("laserDamage", 5L, SettingDataType.LONG, "amount", -20L, 20L, false),
    LASERTARGET_HEAL("lasertargetHeal", 5L, SettingDataType.LONG, "amount", -20L, 20L, false),
    PLAYER_RESET_HEAL("playerResetHeal", 20L, SettingDataType.LONG, "amount", -20L, 20L, false),

    // Capture the flag specific
    FLAG_COUNT("numberOfFlags", 3L, SettingDataType.LONG, "number", 0L, null, false),
    CTF_FLAG_HOLDING_PLAYER_VISIBILITY("flagHoldingPlayerVisibility", CTFFlagHoldingPlayerVisibility.GLOW, SettingDataType.ofEnum(CTFFlagHoldingPlayerVisibility.class), "visibility", null, null, false),
    SEND_FLAG_STOLEN_MESSAGE("sendFlagStolenMessage", true, SettingDataType.BOOL, "value", null, null, false),
    SEND_FLAG_CAPTURED_MESSAGE("sendFlagCapturedMessage", true, SettingDataType.BOOL, "value", null, null, false),
    SEND_TEAM_OUT_MESSAGE("sendTeamOutMessage", true, SettingDataType.BOOL, "value", null, null, false),

    // Musical chairs specific
    PHASE_DURATION("phaseDuration", 2L, SettingDataType.LONG, "minutes", 1L, null, false),
    RESET_SCORES_AT_PHASE_END("resetScoresAtPhaseEnd", false, SettingDataType.BOOL, "value", null, null, false),

    // Elimination specific
    INITIAL_BORDER_SIZE("initialBorderSize", 200L, SettingDataType.LONG, "blocks", 1L, null, false),
    BORDER_SHRINK_DISTANCE("borderShrinkDistance", 10L, SettingDataType.LONG, "blocks", 0L, null, false),
    BORDER_SHRINK_TIME("borderShrinkTime", 10L, SettingDataType.LONG, "seconds", 0L, 59L, false);

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
    private final boolean isGeneral;

    SettingDescription(String name,
                       Object defaultValue,
                       SettingDataType dataType,
                       String settingValueName,
                       Object minValue,
                       Object maxValue,
                       boolean isGeneral) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.dataType = dataType;
        this.settingValueName = settingValueName;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.isGeneral = isGeneral;
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

    public boolean isGeneral() {
        return isGeneral;
    }
}
