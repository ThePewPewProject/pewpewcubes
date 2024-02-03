package de.kleiner3.lasertag.lasertaggame.gamemode.implementation;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.types.ScoreHolding;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.gamemode.GameMode;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.IServerLasertagManager;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * The default game mode. Point hunter. Team-based, time-limited game mode. You earn points for your team
 * by hitting lasertargets or players of other teams. The team with the most points wins.
 *
 * @author Étienne Muser
 */
public class PointHunterGameMode extends GameMode {
    public PointHunterGameMode() {
        super("gameMode.point_hunter", false, true, false);
    }

    @Override
    public List<SettingDescription> getRelevantSettings() {
        var list = super.getRelevantSettings();

        // From time limited
        list.add(SettingDescription.PLAY_TIME);

        // From point based
        list.add(SettingDescription.LASERTARGET_HIT_SCORE);
        list.add(SettingDescription.PLAYER_HIT_SCORE);
        list.add(SettingDescription.DEATH_PENALTY);

        return list;
    }

    @Override
    public void onPreGameStart(MinecraftServer server) {
        super.onPreGameStart(server);

        // Reset scores
        server.getOverworld().getServerLasertagManager().getScoreManager().resetScores();
    }

    @Override
    public void onTick(MinecraftServer server) {
        // On tick not used in this game mode
    }

    @Override
    public void onPlayerHitLasertarget(MinecraftServer server, ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        // Get the managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var scoreManager = gameManager.getScoreManager();
        var settingsManager = gameManager.getSettingsManager();

        scoreManager.onPlayerScored(shooter.getUuid(), settingsManager.<Long>get(SettingDescription.LASERTARGET_HIT_SCORE));
        super.onPlayerHitLasertarget(server, shooter, target);
    }

    @Override
    public void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target) {

        // Get the managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var activationManager = gameManager.getActivationManager();
        var scoreManager = gameManager.getScoreManager();
        var settingsManager = gameManager.getSettingsManager();

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

        var world = server.getOverworld();

        // Deactivate player
        activationManager.deactivate(target.getUuid());

        scoreManager.onPlayerScored(shooter.getUuid(), settingsManager.<Long>get(SettingDescription.PLAYER_HIT_SCORE));
        super.onPlayerHitPlayer(server, shooter, target);
    }

    @Override
    public void onPlayerDeath(MinecraftServer server, ServerPlayerEntity player, DamageSource ignored) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();
        var scoreManager = gameManager.getScoreManager();

        // Give the player the death penalty
        var deathPenalty = -settingsManager.<Long>get(SettingDescription.DEATH_PENALTY);
        scoreManager.onPlayerScored(player.getUuid(), deathPenalty);
    }

    @Override
    public int getWinnerTeamId() {

        // Get the managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();
        var scoreManager = gameManager.getScoreManager();

        return teamsConfigState.getTeams().stream()
                .filter(team -> !teamsManager.getPlayersOfTeam(team).isEmpty())
                .map(team -> new Tuple<>(team, teamsManager.getPlayersOfTeam(team).stream().map(scoreManager::getScore).reduce(Long::sum).orElseThrow()))
                .max(Comparator.comparingLong(Tuple::y))
                .map(tuple -> tuple.x().id())
                .orElse(-1);
    }

    @Override
    public Text getTeamScoreText(TeamDto team) {

        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var scoreManager = gameManager.getScoreManager();
        var teamScore = gameManager.getTeamsManager().getPlayersOfTeam(team).stream()
                .mapToLong(scoreManager::getScore).sum();
        return Text.literal(Long.toString(teamScore));
    }

    @Override
    public Text getPlayerScoreText(UUID playerUuid) {
        return Text.literal(Long.toString(MinecraftClient.getInstance().world.getClientLasertagManager().getScoreManager().getScore(playerUuid)));
    }

    @Override
    public ScoreHolding getTeamFinalScore(TeamDto team, IServerLasertagManager gameManager) {

        var scoreManager = gameManager.getScoreManager();
        var teamScore = (Long) gameManager.getTeamsManager().getPlayersOfTeam(team).stream()
                .mapToLong(scoreManager::getScore).sum();
        return new PointHunterScore(teamScore);
    }

    @Override
    public ScoreHolding getPlayerFinalScore(UUID playerUuid, IServerLasertagManager gameManager) {
        return new PointHunterScore(gameManager.getScoreManager().getScore(playerUuid));
    }

    public static class PointHunterScore implements ScoreHolding {

        private final Long value;

        public PointHunterScore(long value) {
            this.value = value;
        }

        @Override
        public String getValueString() {
            return Long.toString(this.value);
        }

        @Override
        public int compareTo(@NotNull ScoreHolding o) {

            if (!(o instanceof PointHunterScore otherPointHunterScore)) {
                return 0;
            }

            return this.value.compareTo(otherPointHunterScore.value);
        }
    }
}
