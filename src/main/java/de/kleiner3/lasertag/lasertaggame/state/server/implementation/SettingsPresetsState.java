package de.kleiner3.lasertag.lasertaggame.state.server.implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.SettingsState;

import java.util.HashMap;

/**
 * State resembling the presets currently saved by the player
 *
 * @author Étienne Muser
 */
public class SettingsPresetsState extends HashMap<String, SettingsState> {

    public static SettingsPresetsState createNewPresetsMap() {

        var presetsMap = new SettingsPresetsState();

        // Put default presets
        presetsMap.put("default", SettingsState.createBaseSettings());

        return presetsMap;
    }

    public static SettingsPresetsState fromJson(String json) {
        return new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(json, SettingsPresetsState.class);
    }

    public String toJson() { return new Gson().toJson(this); }
}
