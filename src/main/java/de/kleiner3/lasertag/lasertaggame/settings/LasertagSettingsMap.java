package de.kleiner3.lasertag.lasertaggame.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The hash map containing all lasertag settings
 *
 * @author Étienne Muser
 */
public class LasertagSettingsMap implements Map<String, Object> {
    private final HashMap<String, Object> hashMap;

    public LasertagSettingsMap() {
        hashMap = new HashMap<>();
    }

    public static LasertagSettingsMap createDefaultSettings() {
        var settings = new LasertagSettingsMap();

        settings.put(SettingNames.WEAPON_COOLDOWN, 4);
        settings.put(SettingNames.WEAPON_REACH, 50);
        settings.put(SettingNames.SHOW_LASER_RAYS, true);
        settings.put(SettingNames.MAX_TEAM_SIZE, 6);
        settings.put(SettingNames.RENDER_TEAM_LIST, true);
        settings.put(SettingNames.RENDER_TIMER, true);
        settings.put(SettingNames.LASERTARGET_HIT_SCORE, 100);
        settings.put(SettingNames.PLAYER_HIT_SCORE, 20);
        settings.put(SettingNames.START_TIME, 10);
        settings.put(SettingNames.DEACTIVATE_TIME, 5);
        settings.put(SettingNames.LASERTARGET_DEACTIVATE_TIME, 7);
        settings.put(SettingNames.PLAY_TIME, 10);
        settings.put(SettingNames.GEN_STATS_FILE, true);
        settings.put(SettingNames.AUTO_OPEN_STATS_FILE, true);
        settings.put(SettingNames.ORIGIN_SPAWN, true);

        return settings;
    }

    public String toJson() {
        return new Gson().toJson(hashMap);
    }

    public static LasertagSettingsMap fromJson(String json) {
        return new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(json, LasertagSettingsMap.class);
    }

    //region Map implementation

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return hashMap.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return hashMap.get(key);
    }

    @Nullable
    @Override
    public Object put(String key, Object value) {
        return hashMap.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return hashMap.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        hashMap.putAll(m);
    }

    @Override
    public void clear() {
        hashMap.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return hashMap.keySet();
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return hashMap.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return hashMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return hashMap.equals(o);
    }

    @Override
    public int hashCode() {
        return hashMap.hashCode();
    }

    //endregion
}
