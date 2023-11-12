package de.kleiner3.lasertag.lasertaggame.management.deactivation;

import com.google.gson.Gson;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Manages which player is currently deactivated
 *
 * @author Étienne Muser
 */
public class PlayerDeactivatedManager implements IManager {
    private HashMap<UUID, Boolean> deactivatedMap;

    public PlayerDeactivatedManager() {
        deactivatedMap = new HashMap<>();
    }

    //region Public methods

    /**
     * @return If the player is currently deactivated
     */
    public boolean isDeactivated(UUID uuid) {

        // If not already in map
        if (!deactivatedMap.containsKey(uuid)) {
            deactivatedMap.put(uuid, true);
        }

        return deactivatedMap.get(uuid);
    }

    /**
     * Sets if the player is deactivated
     *
     * @param deactivated If the player is deactivated
     */
    @Environment(EnvType.CLIENT)
    public void setDeactivated(UUID uuid, boolean deactivated) {

        LasertagMod.LOGGER.info("[Client] Setting the player with uuid '" + uuid + "' to deactivated: " + deactivated);
        deactivatedMap.put(uuid, deactivated);
    }

    /**
     * Deactivates a player, activates him again after the configured time
     *
     * @param playerUuid The uuid of the player to deactivate
     * @param server     The server on which the game is running
     */

    public void deactivateAndReactivate(UUID playerUuid, MinecraftServer server) {

        this.deactivateAndReactivate(playerUuid, server, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAYER_DEACTIVATE_TIME));
    }

    /**
     * Deactivates a player, activates him again after the specified duration
     *
     * @param playerUuid           The uuid of the player to deactivate
     * @param server               The server on which the game is running
     * @param deactivationDuration The amount of time in seconds the player will be deactivated
     */
    public void deactivateAndReactivate(UUID playerUuid, MinecraftServer server, long deactivationDuration) {

        LasertagMod.LOGGER.info("[Server] Setting the player with uuid '" + playerUuid + "' to deactivated: true for " + deactivationDuration + " seconds.");

        // Deactivate player
        this.setDeactivatedOnServer(server, playerUuid, true);

        // Reactivate player after configured amount of time
        var deactivationThread = ThreadUtil.createScheduledExecutor("lasertag-player-deactivation-thread-%d");
        deactivationThread.schedule(() -> {

            LasertagMod.LOGGER.info("[Server] Setting the player with uuid '" + playerUuid + "' to deactivated: false after timer ran out");
            this.setDeactivatedOnServer(server, playerUuid, false);
            deactivationThread.shutdownNow();
        }, deactivationDuration, TimeUnit.SECONDS);
    }

    /**
     * Deactivates all players without time limit
     *
     * @param server The server on which the game is running
     */
    public void deactivateAll(MinecraftServer server) {

        LasertagMod.LOGGER.info("[Server] Deactivating all players");
        this.setAll(server, true);
    }

    /**
     * Activates all players without time limit
     *
     * @param server The server on which the game is running
     */
    public void activateAll(MinecraftServer server) {

        LasertagMod.LOGGER.info("[Server] Activating all players");
        this.setAll(server, false);
    }

    //endregion

    //region Private methods

    private void setAll(MinecraftServer server, boolean deactivated) {

        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        teamManager.forEachPlayer((team, playerUuid) -> setDeactivatedOnServer(server, playerUuid, deactivated));
    }

    private void setDeactivatedOnServer(MinecraftServer server, UUID playerUuid, boolean deactivated) {

        LasertagMod.LOGGER.info("[Server] Setting the player with uuid '" + playerUuid + "' to deactivated: " + deactivated);
        deactivatedMap.put(playerUuid, deactivated);
        sendDeactivatedToClients(server.getOverworld(), playerUuid, deactivated);

        var player = server.getPlayerManager().getPlayer(playerUuid);

        // Sanity check
        if (player == null) {
            return;
        }

        if (deactivated) {
            player.onDeactivated();
        } else {
            player.onActivated();
        }
    }

    private static void sendDeactivatedToClients(ServerWorld world, UUID uuid, boolean deactivated) {

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeUuid(uuid);
        buf.writeBoolean(deactivated);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.PLAYER_DEACTIVATED_STATUS_CHANGED, buf);
    }

    // endregion

    //region Unused methods

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    public static PlayerDeactivatedManager fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, PlayerDeactivatedManager.class);
    }

    //endregion
}
