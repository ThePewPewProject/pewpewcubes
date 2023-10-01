package de.kleiner3.lasertag.lasertaggame.management;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import de.kleiner3.lasertag.client.screen.LasertagTeamSelectorScreen;
import de.kleiner3.lasertag.lasertaggame.management.deactivation.PlayerDeactivatedManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.LasertagGameModeManager;
import de.kleiner3.lasertag.lasertaggame.management.gui.LasertagHudRenderManager;
import de.kleiner3.lasertag.lasertaggame.management.players.LasertagPlayerNameManager;
import de.kleiner3.lasertag.lasertaggame.management.score.LasertagScoreManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.presets.LasertagSettingsPresetsNameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.LasertagTeamManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamConfigManagerDeserializer;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamDtoSerializer;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;

/**
 * Class to manage the lasertag game. Contains everything which should get synced to the clients.
 *
 * @author Étienne Muser
 */
public class LasertagGameManager implements IManager {
    //region Singleton-ish methods

    private static LasertagGameManager instance = new LasertagGameManager();

    public static LasertagGameManager getInstance() {
        return instance;
    }

    public static void reset() {
        instance.dispose();
        instance = new LasertagGameManager();
    }

    public static void set(LasertagGameManager newManager) {
        instance = newManager;
    }

    //endregion

    //region Private fields

    private PlayerDeactivatedManager deactivatedManager;

    private LasertagSettingsManager settingsManager;

    private LasertagSettingsPresetsNameManager presetsNameManager;

    private LasertagTeamManager teamManager;

    private LasertagScoreManager scoreManager;

    private LasertagHudRenderManager hudRenderManager;

    private LasertagPlayerNameManager playerManager;

    private LasertagGameModeManager gameModeManager;

    //endregion

    private LasertagGameManager() {
        deactivatedManager = new PlayerDeactivatedManager();
        settingsManager = new LasertagSettingsManager();
        presetsNameManager = new LasertagSettingsPresetsNameManager();
        teamManager = new LasertagTeamManager();
        scoreManager = new LasertagScoreManager();
        hudRenderManager = new LasertagHudRenderManager();
        playerManager = new LasertagPlayerNameManager();
        gameModeManager = new LasertagGameModeManager();
    }

    //region Public methods

    public LasertagTeamManager getTeamManager() {
        return teamManager;
    }

    public LasertagSettingsManager getSettingsManager() {
        return settingsManager;
    }

    public LasertagSettingsPresetsNameManager getPresetsNameManager() {
        return presetsNameManager;
    }

    public LasertagHudRenderManager getHudRenderManager() {
        return hudRenderManager;
    }

    public PlayerDeactivatedManager getDeactivatedManager() {
        return deactivatedManager;
    }

    public LasertagScoreManager getScoreManager() {
        return scoreManager;
    }

    public LasertagPlayerNameManager getPlayerManager() { return playerManager; }

    public LasertagGameModeManager getGameModeManager() { return gameModeManager; }

    @Override
    public void dispose() {
        teamManager.dispose();
        settingsManager.dispose();
        hudRenderManager.dispose();
        deactivatedManager.dispose();
    }

    public static LasertagGameManager fromJson(String jsonString) {
        var builder = new GsonBuilder();

        // Set number strategy
        builder.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE);

        // Register team serializer
        builder.registerTypeAdapter(new TypeToken<HashMap<String, TeamDto>>() {}.getType(), TeamConfigManagerDeserializer.getDeserializer());

        return builder.create().fromJson(jsonString, LasertagGameManager.class);
    }

    public void syncToClient(ServerPlayerEntity client, MinecraftServer server) {
        // Serialize to json
        var jsonString = toJson();

        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write json to buffer
        buf.writeString(jsonString);

        // Get if game is running
        var gameRunning = server.getLasertagServerManager().isGameRunning();

        // Write to buffer
        buf.writeBoolean(gameRunning);

        // Send to client
        ServerPlayNetworking.send(client, NetworkingConstants.GAME_MANAGER_SYNC, buf);
    }

    public void reloadTeamConfig(ServerWorld world) {
        teamManager.dispose();
        teamManager = new LasertagTeamManager();

        var teamConfigJson = teamManager.toJson();

        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(teamConfigJson);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.TEAM_CONFIG_RELOADED, buf);
    }

    public void setTeamConfig(LasertagTeamManager newTeamConfig) {
        teamManager.dispose();
        teamManager = newTeamConfig;

        // Try to get the minecraft client
        var client = MinecraftClient.getInstance();
        if (client != null && client.currentScreen instanceof LasertagTeamSelectorScreen lasertagTeamSelectorScreen) {
            lasertagTeamSelectorScreen.resetList();
        }
    }

    //endregion

    //region Private methods

    private String toJson() {
        var builder = new GsonBuilder();

        // Register team serializer
        builder.registerTypeAdapter(TeamDto.class, TeamDtoSerializer.getSerializer());

        return builder.create().toJson(this);
    }

    //endregion
}
