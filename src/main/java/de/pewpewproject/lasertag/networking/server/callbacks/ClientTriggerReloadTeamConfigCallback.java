package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

/**
 * Callback for the client trigger reload team config network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerReloadTeamConfigCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var teamsManager = gameManager.getTeamsManager();
            var playerNamesState = gameManager.getSyncedState().getPlayerNamesState();

            server.execute(() -> {

                var world = server.getOverworld();

                // Throw every player out of his team
                playerNamesState.forEachPlayer((playerUuid) -> {
                    teamsManager.playerLeaveHisTeam(playerUuid);

                    var playerOptional = Optional.ofNullable(world.getPlayerByUuid(playerUuid));
                    playerOptional.ifPresent(playerEntity -> playerEntity.getInventory().clear());
                });

                teamsManager.reloadTeamsConfig();
            });
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerReloadTeamConfigCallback", ex);
            throw ex;
        }
    }
}
