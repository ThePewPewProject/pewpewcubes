package de.kleiner3.lasertag.lasertaggame.management.settings;

/**
 * DTO holding all information about a setting
 *
 * @author Étienne Muser
 */
public enum SettingDescription {

    WEAPON_COOLDOWN("weaponCooldown", 4L, SettingDataType.LONG, "ticks", 0L, null),
    WEAPON_REACH("weaponReach", 50L, SettingDataType.LONG, "blocks", 0L, null),
    SHOW_LASER_RAYS("showLaserRays", true, SettingDataType.BOOL, "value", null, null),
    MAX_TEAM_SIZE("maxTeamSize", 6L, SettingDataType.LONG, "players", 0L, null),
    RENDER_TEAM_LIST("renderTeamList", true, SettingDataType.BOOL, "value", null, null),
    RENDER_TIMER("renderTimer", true, SettingDataType.BOOL, "value", null, null),
    LASERTARGET_HIT_SCORE("lasertargetHitScore", 100L, SettingDataType.LONG, "points", null, null),
    PLAYER_HIT_SCORE("playerHitScore", 20L, SettingDataType.LONG, "points", null, null),
    PREGAME_DURATION("pregameDuration", 10L, SettingDataType.LONG, "seconds", 0L, null),
    PLAYER_DEACTIVATE_TIME("playerDeactivationDuration", 5L, SettingDataType.LONG, "seconds", 0L, null),
    LASERTARGET_DEACTIVATE_TIME("lasertargetDeactivatedDuration", 7L, SettingDataType.LONG, "seconds", 0L, null),
    PLAY_TIME("gameDuration", 10L, SettingDataType.LONG, "minutes", 1L, null),
    GEN_STATS_FILE("generateStatsFile", true, SettingDataType.BOOL, "value", null, null),
    AUTO_OPEN_STATS_FILE("autoOpenStatsFile", true, SettingDataType.BOOL, "value", null, null),
    DO_ORIGIN_SPAWN("doOriginSpawn", true, SettingDataType.BOOL, "value", null, null),
    DEATH_PENALTY("deathPenalty", 500L, SettingDataType.LONG, "points", null, null);

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
