package de.kleiner3.lasertag.lasertaggame.management.settings.presets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.LasertagSettingsMap;

import java.util.HashMap;

/**
 * Hashmap containing all settings presets
 * key: [String] The preset name
 * value: [LasertagSettingsMap] The settings associated with this preset
 *
 * @author Étienne Muser
 */
public class LasertagSettingsPresetsMap extends HashMap<String, LasertagSettingsMap> {

    public static LasertagSettingsPresetsMap createNewPresetsMap() {

        var presetsMap = new LasertagSettingsPresetsMap();

        // Put default presets
        presetsMap.put("default", LasertagSettingsMap.createBaseSettings());

        return presetsMap;
    }

    public static LasertagSettingsPresetsMap fromJson(String json) {
        return new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(json, LasertagSettingsPresetsMap.class);
    }

    public String toJson() { return new Gson().toJson(this); }
}
