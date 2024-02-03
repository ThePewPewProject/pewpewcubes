package de.kleiner3.lasertag.events.callback;

import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Event handler for the player join event
 *
 * @author Étienne Muser
 */
public class PlayerJoinEventHandler {
    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender ignoredSender, MinecraftServer server) {

        // Get the player
        ServerPlayerEntity player = handler.getPlayer();

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var playerNamesState = gameManager.getSyncedState().getPlayerNamesState();
        var settingsManager = gameManager.getSettingsManager();
        var teamsManager = gameManager.getTeamsManager();

        // Add player to player manager
        playerNamesState.savePlayerUsername(player.getUuid(), player.getLasertagUsername());

        // Set to adventure game mode
        player.changeGameMode(GameMode.ADVENTURE);

        // Sync state to the client
        gameManager.syncStateToClient(player);

        // Send player joined event
        sendPlayerJoinedNetworkEvent(server, player.getUuid(), player.getLasertagUsername());

        // If origin spawn setting is disabled
        if (!settingsManager.<Boolean>get(SettingDescription.DO_ORIGIN_SPAWN)) {
            // Dont teleport him to origin
            return;
        }

        // If player is already in a team and game is running (i.e. he got disconnected)
        if (teamsManager.isPlayerInTeam(player.getUuid()) &&
            gameManager.isGameRunning()) {
            // Dont teleport him to origin
            return;
        }

        // Teleport to spawn
        player.requestTeleport(0.5F, 1, 0.5F);

        // Set players spawnpoint
        player.setSpawnPoint(World.OVERWORLD, new BlockPos(0, 1, 0), 0.0F, true, false);
    }

    private static void sendPlayerJoinedNetworkEvent(MinecraftServer server, UUID playerUuid, String playerName) {

        // Create packet byte buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeUuid(playerUuid);
        buf.writeString(playerName);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.PLAYER_JOINED, buf);
    }
}
