package de.kleiner3.lasertag.networking.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.client.LasertagHudOverlay;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.lasertaggame.PlayerDeactivatedManager;
import de.kleiner3.lasertag.lasertaggame.timing.PreGameCountDownTimerTask;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.util.ConverterUtil;
import de.kleiner3.lasertag.util.Tuple;
import de.kleiner3.lasertag.util.serialize.ColorConfigDeserializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class to handle all networking on the client
 *
 * @author Étienne Muser
 */
public class ClientNetworkingHandler {
    public ClientNetworkingHandler() {

    }

    /**
     * Register everything
     */
    public void register() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASER_RAY_SPAWNED, Callbacks::handleLaserRaySpawned);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASERTAG_GAME_TEAM_OR_SCORE_UPDATE, Callbacks::handleTeamOrScoreUpdate);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ERROR_MESSAGE, Callbacks::handleErrorMessage);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_WEAPON_FIRED_SOUND, Callbacks::handleWeaponFiredSoundEvent);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_WEAPON_FAILED_SOUND, Callbacks::handleWeaponFailedSoundEvent);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAY_PLAYER_SCORED_SOUND, Callbacks::handlePlayerScoredSoundEvent);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_STARTED, Callbacks::handleLasertagGameStarted);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAME_OVER, Callbacks::handleLasertagGameOver);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PROGRESS, Callbacks::handleProgress);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASERTAG_SETTINGS_CHANGED, Callbacks::handleLasertagSettingsChanged);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASERTAG_SETTINGS_SYNC, Callbacks::handleLasertagSettingsSync);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASERTAG_TEAMS_SYNC, Callbacks::handleLasertagTeamsSync);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_DEACTIVATED_STATUS_CHANGED, Callbacks::handlePlayerDeactivatedStatusChanged);
    }

    /**
     * Class containing all callbacks needed by the ClientNetworkingHandler
     *
     * @author Étienne Muser
     */
    private static class Callbacks {
        public static void handleLaserRaySpawned(MinecraftClient client,
                                                 ClientPlayNetworkHandler ignoredHandler,
                                                 PacketByteBuf buf,
                                                 PacketSender ignoredResponseSender) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            double endX = buf.readDouble();
            double endY = buf.readDouble();
            double endZ = buf.readDouble();

            Vec3d pos = new Vec3d(x, y, z);
            Vec3d endPos = new Vec3d(endX, endY, endZ);

            float yaw = buf.readFloat();
            float pitch = buf.readFloat();

            int entityId = buf.readInt();
            UUID uuid = buf.readUuid();

            int color = buf.readInt();

            client.execute(() -> {
                LaserRayEntity entity = new LaserRayEntity(client.world, pos, yaw, pitch, color, endPos);
                entity.setId(entityId);
                entity.setUuid(uuid);

                client.world.addEntity(entityId, entity);
            });
        }

        public static void handleErrorMessage(MinecraftClient client,
                                              ClientPlayNetworkHandler ignoredHandler,
                                              PacketByteBuf buf,
                                              PacketSender ignoredResponseSender) {
            client.player.sendMessage(Text.translatable(buf.readString())
                    .fillStyle(Style.EMPTY.withColor(Formatting.RED)), true);
        }

        public static void handleTeamOrScoreUpdate(MinecraftClient ignoredClient,
                                                   ClientPlayNetworkHandler ignoredHandler,
                                                   PacketByteBuf buf,
                                                   PacketSender ignoredResponseSender) {
            LasertagHudOverlay.teamMap = new Gson().fromJson(buf.readString(),
                    new TypeToken<HashMap<String, LinkedList<Tuple<String, Integer>>>>() {
                    }.getType());
        }

        public static void handleWeaponFiredSoundEvent(MinecraftClient client,
                                                       ClientPlayNetworkHandler ignoredHandler,
                                                       PacketByteBuf buf,
                                                       PacketSender ignoredResponseSender) {
            // Get position of sound event
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            // Execute sound playing on main thread to avoid weird exceptions
            client.execute(() -> client.world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F, true));
        }

        public static void handleWeaponFailedSoundEvent(MinecraftClient client,
                                                        ClientPlayNetworkHandler ignoredHandler,
                                                        PacketByteBuf buf,
                                                        PacketSender ignoredResponseSender) {
            // Get position of sound event
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            // Execute sound playing on main thread to avoid weird exceptions
            client.execute(() -> client.world.playSound(x, y, z, SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, true));
        }

        public static void handlePlayerScoredSoundEvent(MinecraftClient client,
                                                        ClientPlayNetworkHandler ignoredHandler,
                                                        PacketByteBuf ignoredBuf,
                                                        PacketSender ignoredResponseSender) {
            // Execute sound playing on main thread to avoid weird exceptions
            client.execute(() -> client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F));
        }

        public static void handleLasertagGameStarted(MinecraftClient ignoredClient,
                                                     ClientPlayNetworkHandler ignoredHandler,
                                                     PacketByteBuf ignoredBuf,
                                                     PacketSender ignoredResponseSender) {
            // TODO: Assert that this method does nothing if game is already running
            LasertagHudOverlay.progress = 0.0;
            LasertagHudOverlay.startingIn = LasertagConfig.getInstance().getStartTime();
            LasertagHudOverlay.shouldRenderNameTags = false;

            // Start pregame count down timer
            var preGameCountDownTimer = Executors.newSingleThreadScheduledExecutor();
            preGameCountDownTimer.scheduleAtFixedRate(new PreGameCountDownTimerTask(preGameCountDownTimer), 1, 1, TimeUnit.SECONDS);
        }

        public static void handleLasertagGameOver(MinecraftClient ignoredClient,
                                                  ClientPlayNetworkHandler ignoredHandler,
                                                  PacketByteBuf ignoredBuf,
                                                  PacketSender ignoredResponseSender) {
            synchronized (LasertagHudOverlay.gameTimerLock) {
                if (LasertagHudOverlay.gameTimer != null) {
                    LasertagHudOverlay.gameTimer.shutdown();
                    LasertagHudOverlay.gameTimer = null;
                    LasertagHudOverlay.gameTime = 0;
                }
            }

            LasertagHudOverlay.shouldRenderNameTags = true;
        }

        public static void handleProgress(MinecraftClient ignoredClient,
                                          ClientPlayNetworkHandler ignoredHandler,
                                          PacketByteBuf buf,
                                          PacketSender ignoredResponseSender) {
            LasertagHudOverlay.progress = buf.readDouble();
        }

        public static void handleLasertagSettingsChanged(MinecraftClient ignoredClient,
                                                         ClientPlayNetworkHandler ignoredHandler,
                                                         PacketByteBuf buf,
                                                         PacketSender ignoredResponseSender) {
            // Read from buffer
            var methodName = buf.readString();
            var value = buf.readString();

            // Convert to primitive type
            var primitive = ConverterUtil.stringToPrimitiveType(value);

            try {
                // Get correct setter method via reflection
                var setter = LasertagConfig.class.getMethod(methodName, MinecraftServer.class, primitive.getClass());

                // Invoke setter method
                setter.invoke(LasertagConfig.getInstance(), null, primitive);
            } catch (NoSuchMethodException e) {
                LasertagMod.LOGGER.error("Couldn't update lasertag config on client side. Setter not found: " + e.getMessage());
            } catch (InvocationTargetException e) {
                LasertagMod.LOGGER.error("Couldn't update lasertag config on client side. Setter could not be invoked: " + e.getMessage());
            } catch (IllegalAccessException e) {
                LasertagMod.LOGGER.error("Couldn't update lasertag config on client side. Setter illegal access: " + e.getMessage());
            }
        }

        public static void handleLasertagSettingsSync(MinecraftClient ignoredClient,
                                                      ClientPlayNetworkHandler ignoredHandler,
                                                      PacketByteBuf buf,
                                                      PacketSender ignoredResponseSender) {
            // Get json string
            var jsonString = buf.readString();

            // Set config
            LasertagConfig.setInstance(new Gson().fromJson(jsonString, LasertagConfig.class));
        }

        public static void handleLasertagTeamsSync(MinecraftClient ignoredClient,
                                                   ClientPlayNetworkHandler ignoredHandler,
                                                   PacketByteBuf buf,
                                                   PacketSender ignoredResponseSender) {
            // Get json string
            var jsonString = buf.readString();

            // get gson builder
            var gsonBuilder = ColorConfigDeserializer.getDeserializer();

            // Parse
            Colors.colorConfig = gsonBuilder.create().fromJson(jsonString, new TypeToken<HashMap<String, Colors.Color>>() {
            }.getType());
        }

        public static void handlePlayerDeactivatedStatusChanged(MinecraftClient ignoredClient,
                                                                ClientPlayNetworkHandler ignoredHandler,
                                                                PacketByteBuf buf,
                                                                PacketSender ignoredResponseSender) {
            // Read from buffer
            var uuid = buf.readUuid();
            var deactivated = buf.readBoolean();

            // Set deactivated status
            PlayerDeactivatedManager.setDeactivated(uuid, deactivated);
        }
    }
}
