package de.pewpewproject.lasertag.lasertaggame.state.management.client.implementation;

import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IGameModeManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.ISettingsManager;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.SettingsState;

/**
 * Implementation of ISettingsManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class SettingsManager implements ISettingsManager {

    private IClientLasertagManager clientManager;
    private final IGameModeManager gameModeManager;

    public SettingsManager(IGameModeManager gameModeManager) {
        this.gameModeManager = gameModeManager;
    }

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public <T> T get(SettingDescription setting) {

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();
        var key = setting.getName();

        // If key not in settings
        if (!clientManager.getSyncedState().getSettingsState().contains(gameModeName, key)) {
            putDefault(key);
        }

        // Get value from dictionary
        return (T) clientManager.getSyncedState().getSettingsState().get(gameModeName, key);
    }

    @Override
    public <T extends Enum<T>> T getEnum(SettingDescription setting) {

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();
        var key = setting.getName();

        // If key not in settings
        if (!clientManager.getSyncedState().getSettingsState().contains(gameModeName, key)) {
            putDefault(key);
        }

        // Get value from dictionary
        var value = clientManager.getSyncedState().getSettingsState().get(gameModeName, key);

        return Enum.valueOf((Class<T>) setting.getDataType().getValueType(), (String)value);
    }

    @Override
    public void set(String newSettingsJson) {
        clientManager.getSyncedState().getSettingsState().fillWith(SettingsState.fromJson(newSettingsJson));
    }

    @Override
    public void set(String settingName, Object newValue) {
        var gameModeName = gameModeManager.getGameMode().getTranslatableName();
        clientManager.getSyncedState().getSettingsState().set(gameModeName, settingName, newValue);
    }

    private void putDefault(String key) {

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();

        // Get default settings
        var defaultSettings = new SettingsState();

        // Get the default value
        var defaultValue = defaultSettings.get(gameModeName, key);

        // Put the default value in this settings
        clientManager.getSyncedState().getSettingsState().set(gameModeName, key, defaultValue);
    }
}
