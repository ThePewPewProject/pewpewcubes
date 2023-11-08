package de.kleiner3.lasertag.lasertaggame.management.gamemode.implementation;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.types.ScoreHolding;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.GameMode;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
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
        LasertagGameManager.getInstance().getScoreManager().resetScores(server.getOverworld());
    }

    @Override
    public void onTick(MinecraftServer server) {
        // On tick not used in this game mode
    }

    @Override
    public void onPlayerHitLasertarget(MinecraftServer server, ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        LasertagGameManager.getInstance().getScoreManager().onPlayerScored(server.getOverworld(), shooter, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.LASERTARGET_HIT_SCORE));
        super.onPlayerHitLasertarget(server, shooter, target);
    }

    @Override
    public void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target) {

        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        var shooterTeam = teamManager.getTeamOfPlayer(shooter.getUuid());
        var targetTeam = teamManager.getTeamOfPlayer(target.getUuid());

        // Check that hit player is not in same team as firing player
        if (shooterTeam.equals(targetTeam)) {
            return;
        }

        // Check if player is deactivated
        if (LasertagGameManager.getInstance().getDeactivatedManager().isDeactivated(target.getUuid())) {
            return;
        }

        var world = server.getOverworld();

        // Deactivate player
        LasertagGameManager.getInstance().getDeactivatedManager().deactivate(target.getUuid(), world, server.getPlayerManager());

        LasertagGameManager.getInstance().getScoreManager().onPlayerScored(world, shooter, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAYER_HIT_SCORE));
        super.onPlayerHitPlayer(server, shooter, target);
    }

    @Override
    public void onPlayerDeath(MinecraftServer server, ServerPlayerEntity player, DamageSource ignored) {
        // Give the player the death penalty
        var deathPenalty = -LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.DEATH_PENALTY);
        LasertagGameManager.getInstance().getScoreManager().onPlayerScored(server.getOverworld(), player, deathPenalty);
    }

    @Override
    public int getWinnerTeamId() {

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var teamManager = gameManager.getTeamManager();
        var scoreManager = gameManager.getScoreManager();

        return teamManager.getTeamMap().entrySet().stream()
                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue().stream().map(scoreManager::getScore).reduce(Long::sum).get()))
                .max(Comparator.comparingLong(Tuple::y))
                .map(tuple -> tuple.x().id())
                .orElse(-1);
    }

    @Override
    public Text getTeamScoreText(TeamDto team) {
        var scoreManager = LasertagGameManager.getInstance().getScoreManager();
        var teamScore = LasertagGameManager.getInstance().getTeamManager().getPlayersOfTeam(team).stream()
                .mapToLong(scoreManager::getScore).sum();
        return Text.literal(Long.toString(teamScore));
    }

    @Override
    public Text getPlayerScoreText(UUID playerUuid) {
        return Text.literal(Long.toString(LasertagGameManager.getInstance().getScoreManager().getScore(playerUuid)));
    }

    @Override
    public ScoreHolding getTeamFinalScore(TeamDto team) {
        var scoreManager = LasertagGameManager.getInstance().getScoreManager();
        var teamScore = (Long) LasertagGameManager.getInstance().getTeamManager().getPlayersOfTeam(team).stream()
                .mapToLong(scoreManager::getScore).sum();
        return new PointHunterScore(teamScore);
    }

    @Override
    public ScoreHolding getPlayerFinalScore(UUID playerUuid) {
        return new PointHunterScore(LasertagGameManager.getInstance().getScoreManager().getScore(playerUuid));
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
