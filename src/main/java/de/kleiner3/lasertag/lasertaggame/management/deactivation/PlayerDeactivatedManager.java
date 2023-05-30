package de.kleiner3.lasertag.lasertaggame.management.deactivation;

import com.google.gson.Gson;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
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
    public void setDeactivated(UUID uuid, boolean deactivated) {
        deactivatedMap.put(uuid, deactivated);
    }

    /**
     * Deactivates a player, activates him again after the configured time
     *
     * @param playerUuid The uuid of the player to deactivate
     * @param world The world which the player is in
     */
    public void deactivate(UUID playerUuid, World world, PlayerManager playerManager) {

        // Deactivate player
        deactivatedMap.put(playerUuid, true);
        sendDeactivatedToClients(world, playerUuid, true);

        // Reactivate player after configured amount of time
        var deactivationThread = ThreadUtil.createScheduledExecutor("lasertag-player-deactivation-thread-%d");
        deactivationThread.schedule(() -> {

            activate(playerUuid, world, playerManager);
            ThreadUtil.attemptShutdown(deactivationThread);
        }, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAYER_DEACTIVATE_TIME), TimeUnit.SECONDS);

        var player = playerManager.getPlayer(playerUuid);

        // Sanity check
        if (player == null) {
            return;
        }

        player.onDeactivated();
    }

    /**
     * Activates the player referenced by uuid
     *
     * @param uuid The uuid of the player to activate
     * @param world The world which the player is in
     */
    public void activate(UUID uuid, World world, PlayerManager playerManager) {
        deactivatedMap.put(uuid, false);
        sendDeactivatedToClients(world, uuid, false);

        var player = playerManager.getPlayer(uuid);

        // Sanity check
        if (player == null) {
            return;
        }

        player.onActivated();
    }

    /**
     * Deactivates a given player
     *
     * @param playerUuid The uuid of the player to deactivate
     * @param world The world he is in
     * @param forever Bool if he should be deactivated without reactivation count down
     */
    public void deactivate(UUID playerUuid, World world, boolean forever, PlayerManager playerManager) {
        if (forever) {
            deactivatedMap.put(playerUuid, true);
            sendDeactivatedToClients(world, playerUuid, true);
        } else {
            deactivate(playerUuid, world, playerManager);
        }
    }

    /**
     * Deactivates all players forever
     *
     * @param world The world the game is in
     */
    public void deactivateAll(ServerWorld world, PlayerManager playerManager) {

        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        teamManager.forEachPlayer((team, playerUuid) -> {
            deactivate(playerUuid, world, true, playerManager);
        });
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    public static PlayerDeactivatedManager fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, PlayerDeactivatedManager.class);
    }

    //endregion

    //region Private methods

    private static void sendDeactivatedToClients(World world, UUID uuid, boolean deactivated) {
        if (world instanceof ServerWorld serverWorld) {

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

            buf.writeUuid(uuid);
            buf.writeBoolean(deactivated);

            ServerEventSending.sendToEveryone(serverWorld, NetworkingConstants.PLAYER_DEACTIVATED_STATUS_CHANGED, buf);
        }
    }

    // endregion
}
