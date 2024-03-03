package de.pewpewproject.lasertag.lasertaggame.state.management.client.implementation;

import de.pewpewproject.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.ILasertargetsManager;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Implementation of ILasertargetsManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class LasertargetsManager implements ILasertargetsManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void setLastHitTime(BlockPos lasertargetPos, long hitTime) {
        clientManager.getSyncedState().getLasertargetState().setLastHitTime(lasertargetPos, hitTime);
    }

    @Override
    public long getLastHitTime(BlockPos lasertargetPos) {
        return clientManager.getSyncedState().getLasertargetState().getLastHitTime(lasertargetPos);
    }

    @Override
    public void setDeactivated(BlockPos lasertargetPos, boolean isDeactivated) {

        var lasertargetsState = clientManager.getSyncedState().getLasertargetState();

        if (isDeactivated) {
            lasertargetsState.setDeactivated(lasertargetPos);
        } else {
            lasertargetsState.setActivated(lasertargetPos);
        }
    }

    @Override
    public boolean isDeactivated(BlockPos lasertargetPos) {
        return clientManager.getSyncedState().getLasertargetState().isDeactivated(lasertargetPos);
    }

    @Override
    public void setHitBy(BlockPos lasertargetPos, UUID playerUuid) {
        clientManager.getSyncedState().getLasertargetState().setHitBy(lasertargetPos, playerUuid);
    }

    @Override
    public void reset() {
        clientManager.getSyncedState().getLasertargetState().reset();
    }

    @Override
    public void resetAlreadyHitBy() {
        clientManager.getSyncedState().getLasertargetState().resetAlreadyHitBy();
    }
}
