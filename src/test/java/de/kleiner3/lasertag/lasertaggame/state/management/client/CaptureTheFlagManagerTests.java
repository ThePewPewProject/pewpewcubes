package de.kleiner3.lasertag.lasertaggame.state.management.client;

import de.kleiner3.lasertag.common.types.ColorDto;
import de.kleiner3.lasertag.lasertaggame.state.management.client.implementation.CaptureTheFlagManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.*;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.CaptureTheFlagState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.GameModeState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.SettingsState;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.UIState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the client capture the flag manager
 *
 * @author Étienne Muser
 */
public class CaptureTheFlagManagerTests {

    @Test
    void test_getNumberOfFlagsEmpty() {
        var testee = new CaptureTheFlagManager();
        testee.setClientManager(new ClientManagerDummy());

        assertEquals(0, testee.getNumberOfFlags(new TeamDto(0, "blabla", new ColorDto(42, 13, 1), null)));
        assertEquals(0, testee.getNumberOfFlags(new TeamDto(1, "sdfdf", new ColorDto(42, 13, 1), null)));
        assertEquals(0, testee.getNumberOfFlags(new TeamDto(2, "sgerhr", new ColorDto(42, 13, 1), null)));
    }

    @Test
    void test_getNumberOfFlagsSet() {
        var testee = new CaptureTheFlagManager();
        testee.setClientManager(new ClientManagerDummy());

        var team1 = new TeamDto(0, "blabla", new ColorDto(42, 42, 42), null);
        var team2 = new TeamDto(1, "other", new ColorDto(6, 9, 42), null);

        assertEquals(0, testee.getNumberOfFlags(team1));
        assertEquals(0, testee.getNumberOfFlags(team2));

        testee.updateTeamFlagCount(team1, 3);
        testee.updateTeamFlagCount(team2, 3);
        assertEquals(3, testee.getNumberOfFlags(team1));
        assertEquals(3, testee.getNumberOfFlags(team2));
    }

    @Test
    void test_getPlayerHoldingFlagTeamEmpty() {
        var testee = new CaptureTheFlagManager();
        testee.setClientManager(new ClientManagerDummy());

        assertTrue(testee.getPlayerHoldingFlagTeam(UUID.randomUUID()).isEmpty());
        assertTrue(testee.getPlayerHoldingFlagTeam(UUID.randomUUID()).isEmpty());
        assertTrue(testee.getPlayerHoldingFlagTeam(UUID.randomUUID()).isEmpty());
    }

    @Test
    void test_getPlayerHoldingFlagTeamSet() {
        var clientManager = new ClientManagerDummy();
        var teamConfigState = clientManager.syncedState.getTeamsConfigState();
        var testee = new CaptureTheFlagManager();
        testee.setClientManager(clientManager);

        var firstUuid = UUID.randomUUID();
        var secondUuid = UUID.randomUUID();

        assertTrue(testee.getPlayerHoldingFlagTeam(firstUuid).isEmpty());
        assertTrue(testee.getPlayerHoldingFlagTeam(secondUuid).isEmpty());

        var firstTeam = teamConfigState.getTeams().get(0);
        var secondTeam = teamConfigState.getTeams().get(1);

        testee.updateFlagHolding(firstUuid, firstTeam.id());
        assertTrue(testee.getPlayerHoldingFlagTeam(firstUuid).isPresent());
        assertEquals(firstTeam.id(), testee.getPlayerHoldingFlagTeam(firstUuid).map(TeamDto::id).orElseThrow());
        assertTrue(testee.getPlayerHoldingFlagTeam(secondUuid).isEmpty());

        testee.updateFlagHolding(secondUuid, secondTeam.id());
        assertTrue(testee.getPlayerHoldingFlagTeam(firstUuid).isPresent());
        assertEquals(firstTeam.id(), testee.getPlayerHoldingFlagTeam(firstUuid).map(TeamDto::id).orElseThrow());
        assertTrue(testee.getPlayerHoldingFlagTeam(secondUuid).isPresent());
        assertEquals(secondTeam.id(), testee.getPlayerHoldingFlagTeam(secondUuid).map(TeamDto::id).orElseThrow());

        testee.removeFlagHolding(secondUuid);
        assertTrue(testee.getPlayerHoldingFlagTeam(firstUuid).isPresent());
        assertEquals(firstTeam.id(), testee.getPlayerHoldingFlagTeam(firstUuid).map(TeamDto::id).orElseThrow());
        assertTrue(testee.getPlayerHoldingFlagTeam(secondUuid).isEmpty());

        testee.removeFlagHolding(firstUuid);

        assertTrue(testee.getPlayerHoldingFlagTeam(firstUuid).isEmpty());
        assertTrue(testee.getPlayerHoldingFlagTeam(secondUuid).isEmpty());
    }

    static class ClientManagerDummy implements IClientLasertagManager {

        private final ISyncedState syncedState = new ISyncedState() {

            private final ICaptureTheFlagState state = new CaptureTheFlagState();

            @Override
            public IActivationState getActivationState() {
                return null;
            }

            @Override
            public ICaptureTheFlagState getCaptureTheFlagState() {
                return state;
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
                return new ITeamsConfigState() {

                    private final List<TeamDto> teams = List.of(new TeamDto(0, "a", null, null), new TeamDto(1, "b", null, null));

                    @Override
                    public void reset() {

                    }

                    @Override
                    public void reload() {

                    }

                    @Override
                    public Optional<TeamDto> getTeamOfId(int id) {
                        return Optional.of(teams.get(id));
                    }

                    @Override
                    public Optional<TeamDto> getTeamOfName(String name) {
                        return Optional.of(name.equals("a") ? teams.get(0) : teams.get(1));
                    }

                    @Override
                    public List<TeamDto> getTeams() {
                        return teams;
                    }

                    @Override
                    public void setTeamConfig(String jsonString) {

                    }

                    @Override
                    public String toJson() {
                        return null;
                    }
                };
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
