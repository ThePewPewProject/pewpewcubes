package de.kleiner3.lasertag.lasertaggame.management.gamemode.implementation;

import de.kleiner3.lasertag.block.Blocks;
import de.kleiner3.lasertag.block.LasertagFlagBlock;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.block.entity.LasertagFlagBlockEntity;
import de.kleiner3.lasertag.common.types.ScoreHolding;
import de.kleiner3.lasertag.common.util.DurationUtils;
import de.kleiner3.lasertag.damage.DamageSources;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.GameMode;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.spawnpoints.LasertagSpawnpointManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import net.minecraft.block.Block;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * The capture the flag game mode. The target of the game is to capture the flag of another team.<br>
 * <br>
 * A team has N flags. If all of your flags have been captured, your team is out and every player of your team
 * gets send to the spectators. The last team with at least one flag left, wins.<br>
 * You pick up a flag of another team by breaking their flag block. The flag counts as captured
 * if you right-click your teams flag. That also means that if your flag is currently stolen, you can not capture the
 * flag of another team. You first must get your own flag back. If a flag got broken by a player all other flags of that
 * team will also disappear. This makes it impossible that multiple flags of a team are stolen at any given point in
 * time. Also, a player can only hold one flag at a time. If a player holds a flag, he holds the flag until he gets
 * reset by being hit by another player, or he captures it fully.<br>
 * If a player gets hit by another player, he loses health (takes damage). If a player loses all his health (dies) he
 * gets deactivated for X seconds, gets teleported back to his spawnpoint and loses the flag he is holding (if he is
 * holding a flag). X is the configured amount of time for the player deactivation duration. The lost flag gets
 * automatically deposited back to the team and can be picked up by another player again. While the player is
 * deactivated he can not be hit again.<br>
 * A player does not naturally regenerate health. A player can regenerate health by hitting lasertargets. lasertargets
 * can be hit multiple times by the same player, but get deactivated after a hit, just like in the Point Hunter Game
 * Mode.<br>
 * In the team list no points will be displayed (as there are no points in this game mode). Instead, beside the
 * team name the number of flags of that team will be shown. Beside the players will be shown if the player currently
 * holds a flag and also of what team that flag is.<br>
 * If a player is holding a flag, his screen will slightly turn into the color of the team he has picked up the
 * flag from. And there will be a text saying, that he is currently holding the flag of that team.<br>
 * There is a setting to choose how a player holding a flag will be highlighted for other players. The options are:<br>
 * - NONE: The player does not get highlighted for other players.<br>
 * - NAMETAG: The nametag of that player will be visible for all other players and will contain an indicator to
 *            what team he is holding the flag of.<br>
 * - GLOW: The player receives the glow effect in the color of the team he is holding the flag of.<br>
 *
 * @author Étienne Muser
 */
public class CaptureTheFlagGameMode extends GameMode {

    private LasertagSpawnpointManager spawnpointManager;

    public CaptureTheFlagGameMode() {
        super("gameMode.capture_the_flag", true, true, true);
    }

    @Override
    public void onTick(MinecraftServer server) {
        // Nothing to do here
    }

    @Override
    public void onPlayerHitLasertarget(MinecraftServer server, ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        long healAmount = LasertagGameManager.getInstance().getSettingsManager().get(SettingDescription.LASERTARGET_HEAL);

        // Regenerate health
        shooter.heal(healAmount);

        super.onPlayerHitLasertarget(server, shooter, target);
    }

    @Override
    public void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target) {

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var teamManager = gameManager.getTeamManager();

        // Get the teams of shooter and target
        var shooterTeam = teamManager.getTeamOfPlayer(shooter.getUuid());
        var targetTeam = teamManager.getTeamOfPlayer(target.getUuid());

        // Check that hit player is not in same team as firing player
        if (shooterTeam.equals(targetTeam)) {
            return;
        }

        // Check if player is deactivated
        if (gameManager.getDeactivatedManager().isDeactivated(target.getUuid())) {
            return;
        }

        // Get the damage amount
        long damageAmount = gameManager.getSettingsManager().get(SettingDescription.LASER_RAY_DAMAGE);

        // Damage the target
        target.damage(DamageSources.laser(shooter), damageAmount);

        super.onPlayerHitPlayer(server, shooter, target);
    }

    @Override
    public void onPlayerDeath(MinecraftServer server, ServerPlayerEntity player, DamageSource source) {

        // If no game is running
        if (!server.getLasertagServerManager().isGameRunning()) {
            return;
        }

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var deactivatedManager = gameManager.getDeactivatedManager();
        var flagManager = gameManager.getFlagManager();

        // If the player got damaged by laser
        if (source.name.equals("laser")) {

            // Get the shooter
            var shooter = (ServerPlayerEntity)source.getAttacker();

            // Heal the shooter
            long healAmount = gameManager.getSettingsManager().get(SettingDescription.PLAYER_RESET_HEAL);
            shooter.heal(healAmount);
        }

        deactivatedManager.deactivate(player.getUuid(),
                server.getOverworld(),
                server.getPlayerManager(),
                LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAYER_DEACTIVATE_TIME));

        // Get the team of the flag the player is currently holding
        var teamOptional = flagManager.getPlayerHoldingFlagTeam(player.getUuid());

        // check if player is not holding a flag
        if (teamOptional.isEmpty()) {
            return;
        }

        // Drop flag
        flagManager.playerDropFlag(server.getOverworld(), player.getUuid());
    }

    @Override
    public void sendPlayersToSpawnpoints(MinecraftServer server, LasertagSpawnpointManager spawnpointManager) {
        super.sendPlayersToSpawnpoints(server, spawnpointManager);

        // Save spawnpoints for placing the flag blocks in onPreGameStart
        this.spawnpointManager = spawnpointManager;
    }

    @Override
    public void onPreGameStart(MinecraftServer server) {
        super.onPreGameStart(server);

        // Reset the flag manager
        LasertagGameManager.getInstance().getFlagManager().reset(server.getOverworld());

        // Set players dont regen health
        var gameRules = server.getGameRules();
        gameRules.get(GameRules.NATURAL_REGENERATION).set(false, server);

        // Get a list of the teams with players in them
        var teams = LasertagGameManager.getInstance().getTeamManager().getTeamMap().entrySet().stream()
                .filter(team -> !team.getValue().isEmpty())
                .map(Map.Entry::getKey);

        // Get the overworld
        var world = server.getOverworld();

        // For every team
        teams.forEach(team -> this.placeFlags(world, team));
    }

    @Override
    public void checkGameOver(MinecraftServer server) {

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var flagManager = gameManager.getFlagManager();
        var teamManager = gameManager.getTeamManager().getTeamConfigManager();

        // Get the number of teams with flags left
        var numberOfTeamsLeft = teamManager.teamConfig.values().stream()
                .filter(team -> flagManager.getNumberOfFlags(team) > 0)
                .count();

        // If only one team is left
        if (numberOfTeamsLeft <= 1) {
            server.getLasertagServerManager().stopLasertagGame();
        }
    }

    @Override
    public void onGameEnd(MinecraftServer server) {
        super.onGameEnd(server);

        // Set players regen health
        var gameRules = server.getGameRules();
        gameRules.get(GameRules.NATURAL_REGENERATION).set(true, server);

        // Get the game managers
        var gameManager = LasertagGameManager.getInstance();
        var teamManager = gameManager.getTeamManager();

        // Every player drop their flag
        teamManager.forEachPlayer((team, playerUuid) ->
                gameManager.getFlagManager().playerDropFlag(server.getOverworld(), playerUuid));

        // Remove all flags of all teams
        teamManager.getTeamConfigManager().teamConfig.values()
                .forEach(team -> removeFlags(server.getOverworld(), team));
    }

    @Override
    public int getWinnerTeamId() {

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var flagManager = gameManager.getFlagManager();
        var teamManager = gameManager.getTeamManager().getTeamConfigManager();

        return teamManager.teamConfig.values().stream()
                .filter(team -> flagManager.getNumberOfFlags(team) > 0)
                // There should always be at least one team left with more than one flag as you can not capture
                // a flag if your own flag is taken away
                .findFirst()
                .map(TeamDto::id)
                .orElse(-1);
    }

    @Override
    public Text getTeamScoreText(TeamDto team) {

        // Get the number of flags of the team
        var numberOfFlags = LasertagGameManager.getInstance().getFlagManager().getNumberOfFlags(team);

        var scoreString = numberOfFlags + "♪";

        return Text.literal(scoreString);
    }

    @Override
    public Text getPlayerScoreText(UUID playerUuid) {

        // Get the team
        var teamOptional = LasertagGameManager.getInstance().getFlagManager().getPlayerHoldingFlagTeam(playerUuid);

        if (teamOptional.isPresent()) {
            var text = Text.literal("♪");
            text.setStyle(text.getStyle().withColor(teamOptional.get().color().getValue()));
            return text;
        }

        return Text.literal("");
    }

    @Override
    public ScoreHolding getTeamFinalScore(TeamDto team) {

        // Get the survive time
        var surviveTimeOptional = LasertagGameManager.getInstance().getFlagManager().getSurviveTime(team);

        return surviveTimeOptional.map(CTFTeamScore::new).orElseGet(() -> new CTFTeamScore(null));
    }

    @Override
    public ScoreHolding getPlayerFinalScore(UUID playerUuid) {

        // Get the number of flags the player captured
        var numberOfFlagsCaptured = LasertagGameManager.getInstance().getFlagManager().getNumberofCapturedFlags(playerUuid);

        return new CTFPlayerScore(numberOfFlagsCaptured);
    }

    public void placeFlags(ServerWorld world, TeamDto team) {

        // Get the spawnpoints for that team
        var teamSpawnpoints = this.spawnpointManager.getSpawnpoints(team);

        // For every spawnpoint
        teamSpawnpoints.forEach(spawnpoint -> {

            // Calculate block positions
            var lowerBlockPos = spawnpoint.up();
            var upperBlockPos = lowerBlockPos.up();

            // Get the block states
            var lowerState = Blocks.LASERTAG_FLAG_BLOCK.getDefaultState();
            var upperState = lowerState.with(LasertagFlagBlock.HALF, DoubleBlockHalf.UPPER);

            // Place the flag
            world.setBlockState(lowerBlockPos, lowerState);
            world.setBlockState(upperBlockPos, upperState);

            // Set team
            ((LasertagFlagBlockEntity)world.getBlockEntity(lowerBlockPos)).setTeamName(team.name());
            ((LasertagFlagBlockEntity)world.getBlockEntity(upperBlockPos)).setTeamName(team.name());

            // Send update event
            world.updateListeners(lowerBlockPos, lowerState, lowerState, Block.NOTIFY_LISTENERS);
            world.updateListeners(upperBlockPos, upperState, upperState, Block.NOTIFY_LISTENERS);
        });
    }

    public void removeFlags(ServerWorld world, TeamDto team) {

        // Get the spawnpoints for that team
        var teamSpawnpoints = this.spawnpointManager.getSpawnpoints(team);

        // If team has no spawnpoints
        if (teamSpawnpoints == null) {
            return;
        }

        // For every spawnpoint
        teamSpawnpoints.forEach(spawnpoint -> {

            // Calculate block positions
            var lowerBlockPos = spawnpoint.up();
            var upperBlockPos = lowerBlockPos.up();

            // Place the flag
            world.setBlockState(lowerBlockPos, net.minecraft.block.Blocks.AIR.getDefaultState());
            world.setBlockState(upperBlockPos, net.minecraft.block.Blocks.AIR.getDefaultState());
        });
    }

    public static class CTFTeamScore implements ScoreHolding {

        private final Long survivedSeconds;

        public CTFTeamScore(Long survivedSeconds) {
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

            if (!(o instanceof CTFTeamScore otherCTFTeamScore)) {
                return 0;
            }

            if (this.survivedSeconds != null && otherCTFTeamScore.survivedSeconds != null) {
                return this.survivedSeconds.compareTo(otherCTFTeamScore.survivedSeconds);
            } else if (this.survivedSeconds == null && otherCTFTeamScore.survivedSeconds == null) {
                return 0;
            } else if (this.survivedSeconds == null) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public static class CTFPlayerScore implements ScoreHolding {

        private final Long capturedFlags;

        public CTFPlayerScore(long capturedFlags) {
            this.capturedFlags = capturedFlags;
        }

        @Override
        public String getValueString() {
            return this.capturedFlags + " flags captured";
        }

        @Override
        public int compareTo(@NotNull ScoreHolding o) {

            if (!(o instanceof CTFPlayerScore otherCTFPlayerScore)) {
                return 0;
            }

            return this.capturedFlags.compareTo(otherCTFPlayerScore.capturedFlags);
        }
    }
}
