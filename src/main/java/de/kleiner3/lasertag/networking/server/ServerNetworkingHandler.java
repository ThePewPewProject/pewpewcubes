package de.kleiner3.lasertag.networking.server;

import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.callbacks.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Class to handle all networking on the server
 *
 * @author Étienne Muser
 */
public class ServerNetworkingHandler {
    public void register() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_HIT_LASERTARGET, new PlayerHitLasertargetCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_HIT_PLAYER, new PlayerHitPlayerCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_RELOAD_TEAM_CONFIG, new ClientTriggerReloadTeamConfigCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_SETTINGS_RESET, new ClientTriggerSettingsResetCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_SETTING_RESET, new ClientTriggerSettingResetCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_SETTING_CHANGE, new ClientTriggerSettingChangeCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_SAVE_PRESET, new ClientTriggerSavePresetCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_LOAD_PRESET, new ClientTriggerLoadPresetCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_DELETE_PRESET, new ClientTriggerDeletePresetCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_LOAD_MAP, new ClientTriggerLoadMapCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_PLAYER_KICK, new ClientTriggerPlayerKickCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_PLAYER_JOIN_TEAM, new ClientTriggerPlayerJoinTeamCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_GAME_MODE_CHANGE, new ClientTriggerGameModeChangeCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CLIENT_TRIGGER_GENERATE_ZONE, new ClientTriggerGenerateZoneCallback());
    }
}
