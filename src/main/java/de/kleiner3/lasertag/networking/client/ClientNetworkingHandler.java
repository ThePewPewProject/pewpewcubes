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
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASERTAG_GAME_TEAM_OR_SCORE_UPDATE, new TeamOrScoreUpdateCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ERROR_MESSAGE, new ErrorMessageCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_WEAPON_FIRED_SOUND, new WeaponFiredSoundEventCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_WEAPON_FAILED_SOUND, new WeaponFailedSoundEventCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_PLAYER_SCORED_SOUND, new PlayerScoredSoundEventCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_STARTED, new LasertagGameStartedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_START_ABORTED, new LasertagGameStartAbortedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_OVER, new LasertagGameOverCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PROGRESS, new ProgressCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASERTAG_SETTINGS_CHANGED, new LasertagSettingsChangedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASERTAG_GAME_MANAGER_SYNC, new LasertagGameManagerSyncCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_DEACTIVATED_STATUS_CHANGED, new PlayerDeactivatedStatusChangedCallback());
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_STATISTICS, new GameStatisticsIncomingCallback());
    }
}
