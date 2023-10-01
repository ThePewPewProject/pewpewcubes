package de.kleiner3.lasertag.networking.server.callbacks;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.GameModes;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Callback for the client trigger game mode change network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerGameModeChangeCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        // Get the game mode translatable name
        var newGameModeTranslatableName = buf.readString();

        // Get the new game mode
        var newGameMode = GameModes.GAME_MODES.get(newGameModeTranslatableName);

        // Set the new game mode
        LasertagGameManager.getInstance().getGameModeManager().setGameMode(server, newGameMode);
    }
}
