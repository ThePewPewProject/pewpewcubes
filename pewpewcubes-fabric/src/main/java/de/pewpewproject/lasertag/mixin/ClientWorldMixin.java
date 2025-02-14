package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IClientLasertagManagerAccessor;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.implementation.*;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.SyncedState;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into the ClientWorld.class to save the client lasertag manager
 *
 * @author Étienne Muser
 */
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin implements IClientLasertagManagerAccessor {

    private IClientLasertagManager clientLasertagManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        var syncedState = new SyncedState();

        var activationManager = new ActivationManager();
        var captureTheFlagManager = new CaptureTheFlagManager();
        var gameModeManager = new GameModeManager();
        var scoreManager = new ScoreManager();
        var settingsManager = new SettingsManager(gameModeManager);
        var settingsPresetsNameManager = new SettingsPresetsNameManager();
        var teamsManager = new TeamsManager();
        var uiManager = new UIStateManager(gameModeManager, settingsManager);
        var eliminationManager = new EliminationManager();
        var lasertargetsManager = new LasertargetsManager();

        clientLasertagManager = new ClientLasertagManager(syncedState,
                activationManager,
                captureTheFlagManager,
                gameModeManager,
                scoreManager,
                settingsManager,
                settingsPresetsNameManager,
                teamsManager,
                uiManager,
                eliminationManager,
                lasertargetsManager);

        // Set the client manager in the sub managers
        activationManager.setClientManager(clientLasertagManager);
        captureTheFlagManager.setClientManager(clientLasertagManager);
        gameModeManager.setClientManager(clientLasertagManager);
        scoreManager.setClientManager(clientLasertagManager);
        settingsManager.setClientManager(clientLasertagManager);
        settingsPresetsNameManager.setClientManager(clientLasertagManager);
        teamsManager.setClientManager(clientLasertagManager);
        uiManager.setClientManager(clientLasertagManager);
        eliminationManager.setClientManager(clientLasertagManager);
        lasertargetsManager.setClientManager(clientLasertagManager);
    }

    @Override
    public IClientLasertagManager getClientLasertagManager() {
        return clientLasertagManager;
    }
}
