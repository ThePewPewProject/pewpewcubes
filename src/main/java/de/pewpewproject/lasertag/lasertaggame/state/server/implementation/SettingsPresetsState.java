package de.pewpewproject.lasertag.lasertaggame.state.server.implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;

import java.util.HashMap;

/**
 * State resembling the presets currently saved by the player
 *
 * @author Étienne Muser
 */
public class SettingsPresetsState extends HashMap<String, SettingsPreset> {

    public static SettingsPresetsState createNewPresetsMap() {

        var presetsMap = new SettingsPresetsState();

        return presetsMap;
    }

    public static SettingsPresetsState fromJson(String json) {
        return new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(json, SettingsPresetsState.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
