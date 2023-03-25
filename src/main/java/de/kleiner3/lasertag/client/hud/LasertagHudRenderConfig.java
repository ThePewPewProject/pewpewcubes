package de.kleiner3.lasertag.client.hud;

import com.google.gson.Gson;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamDto;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.timing.GameCountDownTimerTask;
import de.kleiner3.lasertag.lasertaggame.timing.PreGameCountDownTimerTask;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to hold all lasertag HUD data
 *
 * @author Étienne Muser
 */
public class LasertagHudRenderConfig {
    //region Public fields

    public boolean shouldRenderNameTags = true;

    /**
     * The time in seconds that has already elapsed
     */
    public long gameTime = 0;

    /**
     * Simplified team map to map players and their score to their teams
     */
    public HashMap<String, LinkedList<Tuple<String, Integer>>> teamMap = new HashMap<>();

    public double progress = 0.0;

    public long startingIn = -1;

    // The logical size of the window
    public int width;
    public int wMid;
    public int height;
    public int hMid;

    //endregion

    //region Private fields

    private transient ScheduledExecutorService gameTimer;

    private transient final Object timerLock = new Object();

    private transient ScheduledExecutorService preGameTimer;

    //endregion

    //region Constants

    public static final int progressBarWidth = 100;
    public static final int numTeams = TeamConfigManager.teamConfig.size();
    public static final int boxColor = 0x88000000;
    public static final int startY = 10;
    public static final int boxHeight = 65;
    public static final int boxWidth = 85;
    public static final int margin = 20;
    public static final int textPadding = 1;
    public static final int textHeight = 9;

    //endregion

    public LasertagHudRenderConfig() {
        for (TeamDto t : TeamConfigManager.teamConfig.values()) {
            teamMap.put(t.name(), new LinkedList<>());
        }
    }

    //region Game timer methods

    public void startGameTimer(long gameTime) {
        if (this.gameTimer != null && this.gameTimer.isShutdown() == false) {
            throw new IllegalStateException("this.gameTimer is already running.");
        }

        this.gameTime = gameTime;

        gameTimer = Executors.newSingleThreadScheduledExecutor();
        gameTimer.scheduleAtFixedRate(new GameCountDownTimerTask(), 0, 1, TimeUnit.SECONDS);
    }

    public void stopGameTimer() {
        synchronized (timerLock) {
            if (this.gameTimer == null) {
                return;
            }

            this.gameTimer.shutdown();
            this.gameTimer = null;
            this.gameTime = 0;
        }
    }

    public void startPreGameCountdownTimer(long startingIn) {
        if (this.preGameTimer != null && this.preGameTimer.isShutdown() == false) {
            throw new IllegalStateException("this.preGameTimer is already running.");
        }

        this.startingIn = startingIn;

        preGameTimer = Executors.newSingleThreadScheduledExecutor();
        preGameTimer.scheduleAtFixedRate(new PreGameCountDownTimerTask(), 1, 1, TimeUnit.SECONDS);
    }

    public void stopPreGameCountdownTimer() {
        synchronized (timerLock) {
            if (this.preGameTimer == null) {
                return;
            }

            this.preGameTimer.shutdown();
            this.preGameTimer = null;
            this.startingIn = -1;
        }
    }

    //endregion

    public void syncToPlayer(ServerPlayerEntity player, MinecraftServer server) {
        // Serialize to json
        var json = new Gson().toJson(this);

        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write errorMessage to buffer
        buf.writeString(json);

        // Write if game is running
        buf.writeBoolean(server.isLasertagGameRunning());

        // Send to all clients
        ServerPlayNetworking.send(player, NetworkingConstants.LASERTAG_HUD_SYNC, buf);
    }

    public void dispose() {
        // Stop pre game timer if running
        stopPreGameCountdownTimer();

        // Stop game timer if running
        stopGameTimer();
    }
}
