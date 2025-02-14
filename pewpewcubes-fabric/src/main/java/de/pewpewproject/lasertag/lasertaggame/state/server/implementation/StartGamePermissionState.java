package de.pewpewproject.lasertag.lasertaggame.state.server.implementation;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.UUID;

/**
 * State resembling all players that are permitted to start a game
 *
 * @author Étienne Muser
 */
public class StartGamePermissionState extends HashSet<UUID> {

    public static StartGamePermissionState fromJson(String json) {
        return new Gson().fromJson(json, StartGamePermissionState.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
