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
 * Callback for the client trigger reset team config network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerResetTeamConfigCallback implements ServerPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var teamsManager = gameManager.getTeamsManager();
            var syncedState = gameManager.getSyncedState();
            var teamsConfigState = syncedState.getTeamsConfigState();
            var playerNamesState = gameManager.getSyncedState().getPlayerNamesState();

            // If a game is running
            if (gameManager.isGameRunning()) {
                // Cannot reset team config in-game
                return;
            }

            server.execute(() -> {

                var world = server.getOverworld();

                // Throw every player out of his team
                playerNamesState.forEachPlayer((playerUuid) -> {
                    teamsManager.playerLeaveHisTeam(playerUuid);

                    var playerOptional = Optional.ofNullable(world.getPlayerByUuid(playerUuid));
                    playerOptional.ifPresent(playerEntity -> playerEntity.getInventory().clear());
                });

                teamsConfigState.reset();
            });
        } catch (Exception ex) {

            LasertagMod.LOGGER.error("Error in ClientTriggerResetTeamConfigCallback", ex);
            throw ex;
        }
    }
}
