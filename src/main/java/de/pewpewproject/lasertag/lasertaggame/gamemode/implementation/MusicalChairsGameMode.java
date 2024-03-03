package de.pewpewproject.lasertag.lasertaggame.gamemode.implementation;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.block.entity.LaserTargetBlockEntity;
import de.pewpewproject.lasertag.common.types.ScoreHolding;
import de.pewpewproject.lasertag.common.util.DurationUtils;
import de.pewpewproject.lasertag.lasertaggame.gamemode.PointBasedGameMode;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.IServerLasertagManager;
import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Similar to the point hunter game mode. You shoot lasertargets or players
 * of other teams to gain points.
 * The game consists of Phases. A phase has a constant, configurable length.
 * At the end of each phase the team with the least amount of points gets
 * eliminated. If there is a tie for the last place all teams with that
 * amount of points get eliminated. The game ends when there is only one
 * team left.
 * If there is a tie between all teams left, no team will be eliminated.
 * It is configurable whether the scores of all teams are reset to 0 after
 * each phase.
 * When you hit a lasertarget it gets deactivated for a configured amount of
 * time. You can't hit the same target again until the phase ends. When the
 * phase ends all lasertargets get reset and can be hit again.
 * When you get hit by another player you get deactivated for a configured
 * amount of time.
 *
 * @author Étienne Muser
 */
public class MusicalChairsGameMode extends PointBasedGameMode {

    public MusicalChairsGameMode() {
        super("gameMode.musical_chairs", true, true);
    }

    @Override
    public void onPreGameStart(MinecraftServer server) {
        super.onPreGameStart(server);

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();
        var musicalChairsManager = gameManager.getMusicalChairsManager();

        // Reset game managers
        eliminationManager.reset();
        musicalChairsManager.reset();
    }

    @Override
    public List<SettingDescription> getRelevantSettings() {
        var list = super.getRelevantSettings();

        // From the musical chairs game mode
        list.add(SettingDescription.PHASE_DURATION);
        list.add(SettingDescription.RESET_SCORES_AT_PHASE_END);
        list.add(SettingDescription.SEND_TEAM_OUT_MESSAGE);

        return list;
    }

    @Override
    public void onPlayerHitLasertarget(MinecraftServer server, ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var musicalChairsManager = gameManager.getMusicalChairsManager();
        var settingsManager = gameManager.getSettingsManager();

        // Player scored
        musicalChairsManager.onPlayerScored(shooter.getUuid(), settingsManager.<Long>get(SettingDescription.LASERTARGET_HIT_SCORE));
        super.onPlayerHitLasertarget(server, shooter, target);
    }

    @Override
    public void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var musicalChairsManager = gameManager.getMusicalChairsManager();
        var settingsManager = gameManager.getSettingsManager();
        var teamsManager = gameManager.getTeamsManager();
        var activationManager = gameManager.getActivationManager();

        var shooterTeam = teamsManager.getTeamOfPlayer(shooter.getUuid());
        var targetTeam = teamsManager.getTeamOfPlayer(target.getUuid());

        // Check that hit player is not in same team as firing player
        if (shooterTeam.equals(targetTeam)) {
            return;
        }

        // Check if player is deactivated
        if (activationManager.isDeactivated(target.getUuid())) {
            return;
        }

        // Player scored
        musicalChairsManager.onPlayerScored(shooter.getUuid(), settingsManager.<Long>get(SettingDescription.PLAYER_HIT_SCORE));
        super.onPlayerHitPlayer(server, shooter, target);
    }

    @Override
    public void onTick(MinecraftServer server) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var musicalChairsManager = gameManager.getMusicalChairsManager();

        musicalChairsManager.tick();
    }

    @Override
    public void checkGameOver(MinecraftServer server) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();

        // Get all the teams that are left
        var numberAliveTeams = eliminationManager.getRemainingTeamIds().size();

        LasertagMod.LOGGER.info("[MusicalChairsGameMode] Remaining teams: " + numberAliveTeams);

        // If there ist one or fewer teams left
        if (numberAliveTeams <= 1) {
            gameManager.stopLasertagGame();
        }
    }

    @Override
    public int getWinnerTeamId() {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var eliminationManager = gameManager.getEliminationManager();

        return eliminationManager.getRemainingTeamIds().get(0);
    }

    @Override
    public ScoreHolding getTeamFinalScore(TeamDto team, IServerLasertagManager gameManager) {

        // Get the game managers
        var eliminationManager = gameManager.getEliminationManager();

        return new MusicalChairsTeamScore(eliminationManager.getTeamSurviveTime(team));
    }

    @Override
    public ScoreHolding getPlayerFinalScore(UUID playerUuid, IServerLasertagManager gameManager) {

        // Get the game managers
        var musicalChairsManager = gameManager.getMusicalChairsManager();

        return new MusicalChairsPlayerScore(musicalChairsManager.getPlayerTotalScore(playerUuid));
    }

    private static class MusicalChairsTeamScore implements ScoreHolding {

        private final Long survivedSeconds;

        public MusicalChairsTeamScore(Long survivedSeconds) {
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

            if (!(o instanceof MusicalChairsTeamScore otherMusicalChairsTeamScore)) {
                return 0;
            }

            if (this.survivedSeconds != null && otherMusicalChairsTeamScore.survivedSeconds != null) {
                return this.survivedSeconds.compareTo(otherMusicalChairsTeamScore.survivedSeconds);
            } else if (this.survivedSeconds == null && otherMusicalChairsTeamScore.survivedSeconds == null) {
                return 0;
            } else if (this.survivedSeconds == null) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private static class MusicalChairsPlayerScore implements ScoreHolding {

        private final Long totalScore;

        public MusicalChairsPlayerScore(long totalScore) {
            this.totalScore = totalScore;
        }

        @Override
        public String getValueString() {
            return this.totalScore + " total Points";
        }

        @Override
        public int compareTo(@NotNull ScoreHolding o) {

            if (!(o instanceof MusicalChairsPlayerScore otherMusicalChairsPlayerScore)) {
                return 0;
            }

            return this.totalScore.compareTo(otherMusicalChairsPlayerScore.totalScore);
        }
    }
}
