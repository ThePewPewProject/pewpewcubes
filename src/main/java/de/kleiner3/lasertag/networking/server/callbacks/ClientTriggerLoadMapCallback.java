package de.kleiner3.lasertag.networking.server.callbacks;

import de.kleiner3.lasertag.worldgen.chunkgen.ArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.ProceduralArenaType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.Optional;

/**
 * Callback for the client trigger load map network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerLoadMapCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        // Get the arena name
        var mapTranslatableName = buf.readString();

        var arenaTypeOptional = Arrays.stream(ArenaType.values())
                .filter(m -> m.translatableName.equals(mapTranslatableName))
                .findFirst();

        var proceduralTypeOptional = Arrays.stream(ProceduralArenaType.values())
                .filter(m -> m.translatableName.equals(mapTranslatableName))
                .findFirst();

        // If is procedural map
        if (proceduralTypeOptional.isPresent()) {
            arenaTypeOptional = Optional.of(ArenaType.PROCEDURAL);
        }

        if (arenaTypeOptional.isEmpty()) {

            return;
        }

        var finalArenaTypeOptional = arenaTypeOptional;
        server.execute(() -> {
            server.getLasertagServerManager().getMapManager().loadMap(finalArenaTypeOptional.get(), proceduralTypeOptional.orElse(ProceduralArenaType.SMALL_2V2));
        });
    }
}