package de.kleiner3.lasertag.lasertaggame.management.settings;

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
    private HashMap<String, Object> hashMap;

    public LasertagSettingsMap() {
        hashMap = new HashMap<>();
    }

    public LasertagSettingsMap(LasertagSettingsMap other) {
        hashMap = new HashMap<>(other.hashMap);
    }

    public static LasertagSettingsMap createBaseSettings() {
        var settings = new LasertagSettingsMap();

        for (var setting : SettingDescription.values()) {
            if (setting.getDataType().isEnum()) {
                settings.put(setting.getName(), ((Enum<?>)setting.getBaseValue()).name());
            } else {
                settings.put(setting.getName(), setting.getBaseValue());
            }
        }

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
