package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.team.player.IPlayerRepository;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mixin into the PlayerManager class to implement the IPlayerRepository
 *
 * @see IPlayerRepository
 * @see PlayerManager
 * @author Étienne Muser
 */
@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements IPlayerRepository {

    @Final
    @Shadow
    private Map<UUID, ServerPlayerEntity> playerMap;

    private Map<UUID, String> playerUsernameCache = new HashMap<>();

    @Override
    public String getConsistentPlayerUsername(UUID uuid) {
        // Try to get the player from standard player map
        var player = playerMap.get(uuid);

        // If the player was found
        if (player != null) {
            var username = player.getLasertagUsername();

            // Refresh in cache
            playerUsernameCache.put(player.getUuid(), username);

            // Return
            return username;
        }

        LasertagMod.LOGGER.info("Used player from fallback option.");

        // Use cache as fallback option
        return playerUsernameCache.get(uuid);
    }

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void playerConnectInject(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        // Put player into cache
        playerUsernameCache.put(player.getUuid(), player.getLasertagUsername());
    }
}
