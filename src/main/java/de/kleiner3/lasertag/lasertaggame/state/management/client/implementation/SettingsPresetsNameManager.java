package de.kleiner3.lasertag.lasertaggame.state.management.client.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.kleiner3.lasertag.lasertaggame.state.management.client.ISettingsPresetsNameManager;

import java.util.List;

/**
 * Implementation of ISettingsPresetsNameManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class SettingsPresetsNameManager implements ISettingsPresetsNameManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void addPresetName(String name) {
        clientManager.getSyncedState().getSettingsPresetsNamesState().addPresetName(name);
    }

    @Override
    public void removePresetName(String name) {
        clientManager.getSyncedState().getSettingsPresetsNamesState().removePresetName(name);
    }

    @Override
    public List<String> getSettingsPresetNames() {
        return clientManager.getSyncedState().getSettingsPresetsNamesState().getAllPresetNames();
    }
}
