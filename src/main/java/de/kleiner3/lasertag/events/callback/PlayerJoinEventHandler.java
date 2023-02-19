package de.kleiner3.lasertag.events.callback;

import de.kleiner3.lasertag.lasertaggame.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamConfigManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Event handler for the player join event
 *
 * @author Étienne Muser
 */
public class PlayerJoinEventHandler {
    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender ignoredSender, MinecraftServer server) {
        // Get the player
        ServerPlayerEntity player = handler.getPlayer();

        // Teleport to spawn
        player.requestTeleport(0.5F, 1, 0.5F);

        // Set players spawnpoint
        player.setSpawnPoint(World.OVERWORLD, new BlockPos(0, 1, 0), 0.0F, true, false);

        // Sync settings
        LasertagSettingsManager.syncToPlayer(player);

        // Sync teams
        TeamConfigManager.syncTeamsToClient(player);

        // Sync scores
        server.syncTeamsAndScoresToPlayer(player);
    }
}
