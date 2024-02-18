package de.kleiner3.lasertag.lasertaggame.gamemode.implementation;

import de.kleiner3.lasertag.common.types.ScoreHolding;
import de.kleiner3.lasertag.common.util.DurationUtils;
import de.kleiner3.lasertag.lasertaggame.gamemode.DamageBasedGameMode;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.IServerLasertagManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.TeamsConfigState;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The elimination game mode. Target of this game mode is to eliminate all other teams.
 * <br>
 * If a player hits another player he loses health. If a player loses all his health he
 * gets eliminated and can spectate the game.<br>
 * The game ends when there are only players of one team left.<br>
 * A player does not naturally regenerate health. A player can regenerate health by
 * hitting lasertargets. Lasertargets can be hit multiple times by the same player, but
 * get deactivated after a hit.<br>
 * A player can also regenerate health by eliminating another player.<br>
 * There will be a world border. The border is centered at (0, 0). The initial size of
 * the border can be configured in the settings. Every N minutes (Phase) the border
 * shrinks by X blocks. N and X can be configured in the settings. The border takes C
 * seconds to shrink. C can also be configured.<br>
 *
 * @author Étienne Muser
 */
public class EliminationGameMode extends DamageBasedGameMode {

    public EliminationGameMode() {
        super("gameMode.elimination", true, true);
    }

    @Override
    public List<SettingDescription> getRelevantSettings() {
        var list = super.getRelevantSettings();

        // From elimination specific
        list.add(SettingDescription.SEND_TEAM_OUT_MESSAGE);
        list.add(SettingDescription.PHASE_DURATION);
        list.add(SettingDescription.INITIAL_BORDER_SIZE);
        list.add(SettingDescription.BORDER_SHRINK_DISTANCE);
        list.add(SettingDescription.BORDER_SHRINK_TIME);

        return list;
    }

    @Override
    public Optional<String> checkStartingConditions(MinecraftServer server) {

        // Call on the base class
        var superStartingAbortReasons = super.checkStartingConditions(server);

        // Get the managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var spawnpointManager = gameManager.getSpawnpointManager();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();
        var settingsManager = gameManager.getSettingsManager();

        // Create the string builder
        var builder = new StringBuilder();

        // Append super abort reasons
        superStartingAbortReasons.ifPresent(builder::append);

        // Initialize the abort flag with the super result
        var abort = superStartingAbortReasons.isPresent();

        // Get the initial border size
        var initialBorderSize = settingsManager.<Long>get(SettingDescription.INITIAL_BORDER_SIZE);

        // For every team
        for (var team : teamsConfigState.getTeams()) {

            // Get the players of the team
            var playerUuids = teamsManager.getPlayersOfTeam(team);

            // Skip spectators
            if (team.equals(TeamsConfigState.SPECTATORS)) {

                continue;
            }

            // Skip empty teams
            if (playerUuids.isEmpty()) {

                continue;
            }

            // Get the spawnpoints for the team
            var spawnpoints = spawnpointManager.getSpawnpoints(team);

            // Get if there are spawnpoints outside the border
            var spawnpointsOutsideWorldBorderPresent = spawnpoints.stream()
                    .anyMatch(s -> s.getX() > initialBorderSize ||
                                   s.getX() < -initialBorderSize ||
                                   s.getZ() > initialBorderSize ||
                                   s.getZ() < -initialBorderSize);

            // If the teams spawnpoints are outside the border
            if (spawnpointsOutsideWorldBorderPresent) {
                abort = true;
                builder.append("  *Team '");
                builder.append(team.name());
                builder.append("' has spawnpoints outside the border\n");
            }
        }

        if (abort) {
            return Optional.of(builder.toString());
        }

        return Optional.empty();
    }

    @Override
    public void onPreGameStart(MinecraftServer server) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();
        var settingsManager = gameManager.getSettingsManager();

        // Reset game managers
        eliminationManager.reset();

        // Set the world border
        var worldBorder = server.getOverworld().getWorldBorder();
        worldBorder.setCenter(0.5, 0.5);
        worldBorder.setSize(settingsManager.<Long>get(SettingDescription.INITIAL_BORDER_SIZE) * 2 + 1);

        super.onPreGameStart(server);
    }

    @Override
    public void onPlayerDeath(MinecraftServer server, ServerPlayerEntity player, DamageSource source) {
        super.onPlayerDeath(server, player, source);

        UUID shooterUuid = null;

        // If the player died through a laser
        if (source.name.equals("laser")) {

            // Get the shooter
            var shooter = (ServerPlayerEntity) source.getAttacker();

            // Get the shooters uuid
            shooterUuid = shooter.getUuid();
        }
        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();

        // Eliminate the player
        eliminationManager.eliminatePlayer(player.getUuid(), shooterUuid);

        checkGameOver(server);
    }

    @Override
    public void checkGameOver(MinecraftServer server) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();
        var teamsManager = gameManager.getTeamsManager();
        var teamsConfigState = gameManager.getSyncedState().getTeamsConfigState();

        // Get the not eliminated players
        var notEliminatedTeams = teamsConfigState.getTeams().stream()
                .filter(t -> t != TeamsConfigState.SPECTATORS)
                .filter(t -> teamsManager.getPlayersOfTeam(t).stream().anyMatch(eliminationManager::isPlayerNotEliminated))
                .count();

        // If the game is not over
        if (notEliminatedTeams > 1) {
            return;
        }

        // Reset border
        var worldBorder = server.getOverworld().getWorldBorder();
        worldBorder.setSize(Integer.MAX_VALUE - 1);

        // Stop the game
        gameManager.stopLasertagGame();
    }

    @Override
    public void onTick(MinecraftServer server) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();

        eliminationManager.tick();
    }

    @Override
    public int getWinnerTeamId() {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();

        return eliminationManager.getRemainingTeamIds().get(0);
    }

    @Override
    public Text getTeamScoreText(TeamDto team) {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();
        var teamsManager = gameManager.getTeamsManager();

        // Get the team score
        var teamScore = teamsManager.getPlayersOfTeam(team).stream()
                .mapToLong(eliminationManager::getEliminationCount)
                .sum();

        return Text.literal(Long.toString(teamScore));
    }

    @Override
    public Text getPlayerScoreText(UUID playerUuid) {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();

        // Get the players elimination count
        var eliminationCount = eliminationManager.getEliminationCount(playerUuid);

        return Text.literal(Long.toString(eliminationCount));
    }

    @Override
    public ScoreHolding getTeamFinalScore(TeamDto team, IServerLasertagManager gameManager) {

        // Get the game managers
        var eliminationManager = gameManager.getEliminationManager();

        return new EliminationTeamScore(eliminationManager.getTeamSurviveTime(team));
    }

    @Override
    public ScoreHolding getPlayerFinalScore(UUID playerUuid, IServerLasertagManager gameManager) {

        // Get the game managers
        var eliminationManager = gameManager.getEliminationManager();

        return new EliminationPlayerScore(eliminationManager.getPlayerSurviveTime(playerUuid), eliminationManager.getPlayerEliminationCount(playerUuid));
    }

    private static class EliminationTeamScore implements ScoreHolding {

        private final Long survivedSeconds;

        public EliminationTeamScore(Long survivedSeconds) {
            this.survivedSeconds = survivedSeconds;
        }

        @Override
        public String getValueString() {

            if (survivedSeconds != null) {
                return "survived " + DurationUtils.toMinuteString(Duration.ofSeconds(this.survivedSeconds)) + " minutes";
            } else {
                return "survived to the end";
            }
        }

        @Override
        public int compareTo(@NotNull ScoreHolding o) {

            if (!(o instanceof EliminationTeamScore otherEliminationTeamScore)) {
                return 0;
            }

            if (this.survivedSeconds != null && otherEliminationTeamScore.survivedSeconds != null) {
                return this.survivedSeconds.compareTo(otherEliminationTeamScore.survivedSeconds);
            } else if (this.survivedSeconds == null && otherEliminationTeamScore.survivedSeconds == null) {
                return 0;
            } else if (this.survivedSeconds == null) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private static class EliminationPlayerScore implements ScoreHolding {

        private final Long survivedSeconds;
        private final Long eliminationCount;

        public EliminationPlayerScore(Long survivedSeconds, long eliminationCount) {
            this.survivedSeconds = survivedSeconds;
            this.eliminationCount = eliminationCount;
        }

        @Override
        public String getValueString() {
            var builder = new StringBuilder();

            builder.append(eliminationCount);
            builder.append(" eliminations - ");

            if (survivedSeconds != null) {
                builder.append("survived ");
                builder.append(DurationUtils.toMinuteString(Duration.ofSeconds(this.survivedSeconds)));
                builder.append(" minutes");
            } else {
                builder.append("survived to the end");
            }

            return builder.toString();
        }

        @Override
        public int compareTo(@NotNull ScoreHolding o) {

            if (!(o instanceof EliminationPlayerScore otherEliminationPlayerScore)) {
                return 0;
            }

            if (this.survivedSeconds != null && otherEliminationPlayerScore.survivedSeconds != null) {
                return this.survivedSeconds.compareTo(otherEliminationPlayerScore.survivedSeconds);
            } else if (this.survivedSeconds == null && otherEliminationPlayerScore.survivedSeconds == null) {
                return 0;
            } else if (this.survivedSeconds == null) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
