package de.kleiner3.lasertag.networking.client;

import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.client.callbacks.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Class to handle all networking on the client
 *
 * @author Étienne Muser
 */
public class ClientNetworkingHandler {
    public void register() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASER_RAY_SPAWNED, new LaserRaySpawnedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ERROR_MESSAGE, new ErrorMessageCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_PLAYER_SCORED_SOUND, new PlayerScoredSoundEventCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_PLAYER_DEACTIVATED_SOUND, new PlayerDeactivatedSoundEventCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_PLAYER_ACTIVATED_SOUND, new PlayerActivatedSoundEventCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.SCORE_UPDATE, new ScoreUpdateCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.TEAM_UPDATE, new TeamUpdateCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_STARTED, new LasertagGameStartedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_START_ABORTED, new LasertagGameStartAbortedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_OVER, new LasertagGameOverCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PROGRESS, new ProgressCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.SCORE_RESET, new ScoreResetCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.SETTING_CHANGED, new LasertagSettingChangedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.SETTINGS_CHANGED, new LasertagSettingsChangedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_MANAGER_SYNC, new LasertagGameManagerSyncCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_DEACTIVATED_STATUS_CHANGED, new PlayerDeactivatedStatusChangedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_STATISTICS, new GameStatisticsIncomingCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_JOINED, new PlayerJoinedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.MAP_LOADING_EVENT, new MapLoadingEventCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.SETTINGS_PRESET_ADDED, new SettingsPresetNameAddedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.SETTINGS_PRESET_REMOVED, new SettingsPresetNameRemovedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.TEAM_CONFIG_RELOADED, new TeamConfigReloadedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_MODE_SYNC, new GameModeSyncCallback());
    }
}
