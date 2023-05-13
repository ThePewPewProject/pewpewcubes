package de.kleiner3.lasertag.lasertaggame.management.color;

import de.kleiner3.lasertag.lasertaggame.management.IManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

public class PlayerColorManager implements IManager {

    private HashMap<String, Integer> playerColorMap;

    public PlayerColorManager() {
        playerColorMap = new HashMap<>();
    }

    public void put(String playerUsername, Integer playerColor) {
        playerColorMap.put(playerUsername, playerColor);
    }

    public Integer get(String playerUsername) {
        return playerColorMap.get(playerUsername);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    @Override
    public void syncToClient(ServerPlayerEntity client, MinecraftServer server) {
        // Do not sync!
        throw new UnsupportedOperationException("PlayerColorManager should not be synced on its own.");
    }
}
