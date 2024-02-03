package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.state.management.server.IServerLasertagManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.IServerLasertagManagerAccessor;
import de.kleiner3.lasertag.lasertaggame.state.management.server.implementation.*;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation.*;
import de.kleiner3.lasertag.lasertaggame.state.server.implementation.SettingsPresetsState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.SyncedState;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into the ServerWorld.class to save the server lasertag manager
 *
 * @author Étienne Muser
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IServerLasertagManagerAccessor {

    private IServerLasertagManager serverLasertagManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        var world = (ServerWorld)(Object)this;
        var server = world.getServer();

        var syncedState = new SyncedState();

        var spawnpointManager = new SpawnpointManager(syncedState.getTeamsConfigState());
        var blockTickManager = new BlockTickManager(server);
        var arenaManager = new ArenaManager(server, spawnpointManager, syncedState.getPlayerNamesState(), blockTickManager);
        var lasertargetManager = new LasertargetManager();
        var musicManager = new MusicManager(server);
        var settingsManager = new SettingsManager(server, syncedState.getSettingsState());
        var gameModeManager = new GameModeManager(syncedState.getGameModeState(), server, settingsManager);
        settingsManager.setGameModeManager(gameModeManager);
        var teamsManager = new TeamsManager(syncedState.getTeamsState(), syncedState.getTeamsConfigState(), server, settingsManager);
        var scoreManager = new ScoreManager(syncedState.getScoreState(), server);
        var uiStateManager = new UIStateManager(syncedState.getUIState(), gameModeManager, settingsManager);
        var captureTheFlagManager = new CaptureTheFlagManager(world, syncedState.getCaptureTheFlagState(), settingsManager, gameModeManager, syncedState.getUIState(), teamsManager, syncedState.getTeamsConfigState());
        var activationManager = new ActivationManager(syncedState.getActivationState(), server, settingsManager, syncedState.getPlayerNamesState());
        var settingsPresetsNameManager = new SettingsPresetsNameManager(syncedState.getSettingsPresetsNamesState(), server);
        var settingsPresetsManager = new SettingsPresetsManager(new SettingsPresetsState(), settingsPresetsNameManager, settingsManager);

        serverLasertagManager = new ServerLasertagManager(server,
                syncedState,
                arenaManager,
                blockTickManager,
                lasertargetManager,
                musicManager,
                settingsPresetsManager,
                spawnpointManager,
                activationManager,
                captureTheFlagManager,
                gameModeManager,
                scoreManager,
                settingsManager,
                settingsPresetsNameManager,
                teamsManager,
                uiStateManager);
    }

    @Override
    public IServerLasertagManager getServerLasertagManager() {
        return serverLasertagManager;
    }
}
