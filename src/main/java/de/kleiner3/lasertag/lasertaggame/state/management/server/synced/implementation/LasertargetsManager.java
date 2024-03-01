package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ILasertargetsManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.ILasertargetState;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of ILasertargetsManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class LasertargetsManager implements ILasertargetsManager {

    private final ILasertargetState lasertargetState;
    private final ISettingsManager settingsManager;
    private final MinecraftServer server;

    public LasertargetsManager(ILasertargetState lasertargetState, ISettingsManager settingsManager, MinecraftServer server) {
        this.lasertargetState = lasertargetState;
        this.settingsManager = settingsManager;
        this.server = server;
    }

    @Override
    public void setLastHitTime(BlockPos lasertargetPos, long hitTime) {

        lasertargetState.setLastHitTime(lasertargetPos, hitTime);

        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeBlockPos(lasertargetPos);
        buf.writeLong(hitTime);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.SET_LAST_HIT_TIME, buf);
    }

    @Override
    public void deactivate(BlockPos lasertargetPos) {

        setDeactivated(lasertargetPos, true);

        // Reactivate after configured amount of seconds
        var deactivationThread = ThreadUtil.createScheduledExecutor("server-lasertag-target-deactivation-thread-%d");
        deactivationThread.schedule(() -> {

            setDeactivated(lasertargetPos, false);

            deactivationThread.shutdownNow();
        }, settingsManager.<Long>get(SettingDescription.LASERTARGET_DEACTIVATE_TIME), TimeUnit.SECONDS);
    }

    @Override
    public boolean isDeactivated(BlockPos lasertargetPos) {
        return lasertargetState.isDeactivated(lasertargetPos);
    }

    @Override
    public void setHitBy(BlockPos lasertargetPos, UUID playerUuid) {

        lasertargetState.setHitBy(lasertargetPos, playerUuid);

        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeBlockPos(lasertargetPos);
        buf.writeUuid(playerUuid);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.SET_HIT_BY, buf);
    }

    @Override
    public boolean isAlreadyHitBy(BlockPos lasertargetPos, UUID playerUuid) {
        return lasertargetState.isAlreadyHitBy(lasertargetPos, playerUuid);
    }

    @Override
    public void reset() {

        lasertargetState.reset();
        ServerEventSending.sendToEveryone(server, NetworkingConstants.LASERTARGETS_RESET, PacketByteBufs.empty());
    }

    @Override
    public void resetAlreadyHitBy() {

        lasertargetState.resetAlreadyHitBy();
        ServerEventSending.sendToEveryone(server, NetworkingConstants.LASERTARGETS_ALREADY_HIT_BY_RESET, PacketByteBufs.empty());
    }

    private void setDeactivated(BlockPos lasertargetPos, boolean isDeactivated) {

        if (isDeactivated) {
            lasertargetState.setDeactivated(lasertargetPos);
        } else {
            lasertargetState.setActivated(lasertargetPos);
        }

        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeBlockPos(lasertargetPos);
        buf.writeBoolean(isDeactivated);

        ServerEventSending.sendToEveryone(server, NetworkingConstants.SET_LASERTARGET_DEACTIVATED, buf);
    }
}
