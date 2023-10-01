package de.kleiner3.lasertag.lasertaggame.management.gamemode;

import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

/**
 * Manages the lasertag game mode.
 *
 * @author Étienne Muser
 */
public class LasertagGameModeManager implements IManager {

    // Store the current game mode as a string to work around some wierd GSON stuff
    private String currentGameModeTranslatableName = GameModes.POINT_HUNTER_GAME_MODE.getTranslatableName();

    public void setGameMode(MinecraftServer s, GameMode newGameMode) {
        this.currentGameModeTranslatableName = newGameMode.getTranslatableName();
        sync(s, newGameMode);
    }

    public GameMode getGameMode() {
        return GameModes.GAME_MODES.get(this.currentGameModeTranslatableName);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    private static void sync(MinecraftServer server, GameMode newGameMode) {
        // Check if this is executed on client
        if (server == null) {
            // Do not sync on client
            return;
        }

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(newGameMode.getTranslatableName());

        ServerEventSending.sendToEveryone(server.getOverworld(), NetworkingConstants.GAME_MODE_SYNC, buf);
    }
}
