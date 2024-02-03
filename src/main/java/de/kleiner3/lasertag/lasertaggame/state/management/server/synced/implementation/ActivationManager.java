package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IActivationManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.IActivationState;
import de.kleiner3.lasertag.lasertaggame.state.synced.IPlayerNamesState;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the IActivationManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class ActivationManager implements IActivationManager {

    private final IActivationState activationState;
    private final MinecraftServer server;

    private final ISettingsManager settingsManager;

    private final IPlayerNamesState playerNamesState;

    public ActivationManager(IActivationState activationState,
                             MinecraftServer server,
                             ISettingsManager settingsManager,
                             IPlayerNamesState playerNamesState) {
        this.activationState = activationState;
        this.server = server;
        this. settingsManager = settingsManager;
        this.playerNamesState = playerNamesState;
    }

    @Override
    public boolean isDeactivated(UUID playerUuid) {
        return !activationState.isActivated(playerUuid);
    }

    @Override
    public void deactivate(UUID playerUuid) {
        deactivate(playerUuid, settingsManager.<Long>get(SettingDescription.PLAYER_DEACTIVATE_TIME));
    }

    @Override
    public void deactivate(UUID playerUuid, long deactivationDuration) {

        // Deactivate player
        setPlayerDeactivated(playerUuid);

        // Reactivate player after configured amount of time
        var deactivationThread = ThreadUtil.createScheduledExecutor("server-lasertag-player-deactivation-thread-%d");
        deactivationThread.schedule(() -> {

            setPlayerActivated(playerUuid);
            deactivationThread.shutdownNow();
        }, deactivationDuration, TimeUnit.SECONDS);
    }

    @Override
    public void deactivateAll() {

        playerNamesState.forEachPlayer(this::setPlayerDeactivated);
    }

    @Override
    public void activateAll() {

        playerNamesState.forEachPlayer(this::setPlayerActivated);
    }

    private void setPlayerDeactivated(UUID playerUuid) {

        activationState.deactivatePlayer(playerUuid);
        sendDeactivatedToClients(playerUuid, true);

        var player = server.getPlayerManager().getPlayer(playerUuid);

        // Sanity check
        if (player == null) {
            LasertagMod.LOGGER.warn("[Server] ActivationManager: Player was null on deactivation.");
            return;
        }

        player.onDeactivated();
    }

    private void setPlayerActivated(UUID playerUuid) {

        activationState.activatePlayer(playerUuid);
        sendDeactivatedToClients(playerUuid, false);

        var player = server.getPlayerManager().getPlayer(playerUuid);

        // Sanity check
        if (player == null) {
            LasertagMod.LOGGER.warn("[Server] ActivationManager: Player was null on activation.");
            return;
        }

        player.onActivated();
    }

    private void sendDeactivatedToClients(UUID uuid, boolean deactivated) {

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeUuid(uuid);
        buf.writeBoolean(deactivated);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.PLAYER_DEACTIVATED_STATUS_CHANGED, buf);
    }
}
