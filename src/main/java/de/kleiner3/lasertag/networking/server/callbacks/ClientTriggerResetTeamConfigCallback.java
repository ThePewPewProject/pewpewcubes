package de.kleiner3.lasertag.networking.server.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
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

            server.execute(() -> {

                var world = server.getOverworld();
                var teamManager = LasertagGameManager.getInstance().getTeamManager();
                var teamConfigManager = teamManager.getTeamConfigManager();

                // Throw every player out of his team
                teamManager.forEachPlayer((team, playerUuid) -> {
                    teamManager.playerLeaveHisTeam(world, playerUuid);

                    var playerOptional = Optional.ofNullable(world.getPlayerByUuid(playerUuid));
                    playerOptional.ifPresent(playerEntity -> playerEntity.getInventory().clear());
                });

                teamConfigManager.reset();
            });
        } catch (Exception ex) {

            LasertagMod.LOGGER.error("Error in ClientTriggerResetTeamConfigCallback", ex);
            throw ex;
        }
    }
}
