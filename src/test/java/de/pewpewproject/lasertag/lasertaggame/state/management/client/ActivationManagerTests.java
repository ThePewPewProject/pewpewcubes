package de.pewpewproject.lasertag.lasertaggame.state.management.client;

import de.pewpewproject.lasertag.lasertaggame.state.management.client.implementation.ActivationManager;
import de.pewpewproject.lasertag.lasertaggame.state.synced.*;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the client activation manager
 *
 * @author Étienne Muser
 */
public class ActivationManagerTests {

    @Test
    void test_isDeactivatedEmpty() {
        var testee = new ActivationManager();
        testee.setClientManager(new ClientManagerDummy());

        assertTrue(testee.isDeactivated(UUID.randomUUID()));
        assertTrue(testee.isDeactivated(UUID.randomUUID()));
        assertTrue(testee.isDeactivated(UUID.randomUUID()));
    }

    @Test
    void test_isDeactivatedActivateOne() {
        var testee = new ActivationManager();
        testee.setClientManager(new ClientManagerDummy());

        var firstUuid = UUID.randomUUID();

        assertTrue(testee.isDeactivated(firstUuid));

        testee.setDeactivated(firstUuid, false);
        assertFalse(testee.isDeactivated(firstUuid));
    }

    @Test
    void test_isDeactivatedActivateTwo() {
        var testee = new ActivationManager();
        testee.setClientManager(new ClientManagerDummy());

        var firstUuid = UUID.randomUUID();
        var secondUuid = UUID.randomUUID();

        assertTrue(testee.isDeactivated(firstUuid));
        assertTrue(testee.isDeactivated(secondUuid));

        testee.setDeactivated(firstUuid, false);
        assertFalse(testee.isDeactivated(firstUuid));
        assertTrue(testee.isDeactivated(secondUuid));

        testee.setDeactivated(secondUuid, false);
        assertFalse(testee.isDeactivated(firstUuid));
        assertFalse(testee.isDeactivated(secondUuid));
    }

    @Test
    void test_isDeactivatedActivateDeactivateTwo() {
        var testee = new ActivationManager();
        testee.setClientManager(new ClientManagerDummy());

        var firstUuid = UUID.randomUUID();
        var secondUuid = UUID.randomUUID();

        assertTrue(testee.isDeactivated(firstUuid));
        assertTrue(testee.isDeactivated(secondUuid));

        testee.setDeactivated(firstUuid, false);
        assertFalse(testee.isDeactivated(firstUuid));
        assertTrue(testee.isDeactivated(secondUuid));

        testee.setDeactivated(secondUuid, false);
        assertFalse(testee.isDeactivated(firstUuid));
        assertFalse(testee.isDeactivated(secondUuid));

        testee.setDeactivated(firstUuid, true);
        assertTrue(testee.isDeactivated(firstUuid));
        assertFalse(testee.isDeactivated(secondUuid));

        testee.setDeactivated(secondUuid, true);
        assertTrue(testee.isDeactivated(firstUuid));
        assertTrue(testee.isDeactivated(secondUuid));
    }

    static class ClientManagerDummy implements IClientLasertagManager {

        private final ISyncedState syncedState = new ISyncedState() {

            private final IActivationState activationState = new ActivationState();

            @Override
            public IActivationState getActivationState() {
                return activationState;
            }

            @Override
            public ICaptureTheFlagState getCaptureTheFlagState() {
                return null;
            }

            @Override
            public GameModeState getGameModeState() {
                return null;
            }

            @Override
            public IPlayerNamesState getPlayerNamesState() {
                return null;
            }

            @Override
            public IScoreState getScoreState() {
                return null;
            }

            @Override
            public ISettingsPresetsNamesState getSettingsPresetsNamesState() {
                return null;
            }

            @Override
            public SettingsState getSettingsState() {
                return null;
            }

            @Override
            public ITeamsState getTeamsState() {
                return null;
            }

            @Override
            public ITeamsConfigState getTeamsConfigState() {
                return null;
            }

            @Override
            public UIState getUIState() {
                return null;
            }

            @Override
            public IEliminationState getEliminationState() {
                return null;
            }

            @Override
            public ILasertargetState getLasertargetState() {
                return null;
            }

            @Override
            public String toJson() {
                return null;
            }
        };

        @Override
        public IActivationManager getActivationManager() {
            return null;
        }

        @Override
        public ICaptureTheFlagManager getCaptureTheFlagManager() {
            return null;
        }

        @Override
        public IGameModeManager getGameModeManager() {
            return null;
        }

        @Override
        public IScoreManager getScoreManager() {
            return null;
        }

        @Override
        public ISettingsManager getSettingsManager() {
            return null;
        }

        @Override
        public ISettingsPresetsNameManager getSettingsPresetsNameManager() {
            return null;
        }

        @Override
        public ITeamsManager getTeamsManager() {
            return null;
        }

        @Override
        public IUIStateManager getUIStateManager() {
            return null;
        }

        @Override
        public IEliminationManager getEliminationManager() {
            return null;
        }

        @Override
        public ILasertargetsManager getLasertargetsManager() {
            return null;
        }

        @Override
        public ISyncedState getSyncedState() {
            return syncedState;
        }

        @Override
        public void setSyncedState(ISyncedState syncedState) {

        }
    }
}
