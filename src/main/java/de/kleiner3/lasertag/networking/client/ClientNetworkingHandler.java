package de.kleiner3.lasertag.networking.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.kleiner3.lasertag.client.LasertagHudOverlay;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.util.Tuple;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskQueue;
import org.apache.commons.compress.harmony.pack200.NewAttributeBands;

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
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.PROGRESS, Callbacks::handleProgress);
    }

    /**
     * Class containing all callbacks needed by the ClientNetworkingHandler
     *
     * @author Étienne Muser
     */
    private class Callbacks {
        public static void handleLaserRaySpawned(MinecraftClient client,
                                                 ClientPlayNetworkHandler handler,
                                                 PacketByteBuf buf,
                                                 PacketSender responseSender) {
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
                                              ClientPlayNetworkHandler handler,
                                              PacketByteBuf buf,
                                              PacketSender responseSender) {
            client.player.sendMessage(Text.translatable(buf.readString())
                    .fillStyle(Style.EMPTY.withColor(Formatting.RED)), true);
        }

        public static void handleTeamOrScoreUpdate(MinecraftClient client,
                                                   ClientPlayNetworkHandler handler,
                                                   PacketByteBuf buf,
                                                   PacketSender responseSender) {
            LasertagHudOverlay.teamMap = new Gson().fromJson(buf.readString(),
                    new TypeToken<HashMap<Colors, LinkedList<Tuple<String, Integer>>>>() {
                    }.getType());
        }

        public static void handleWeaponFiredSoundEvent(MinecraftClient client,
                                                       ClientPlayNetworkHandler handler,
                                                       PacketByteBuf buf,
                                                       PacketSender responseSender) {
            // Get position of sound event
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            // Execute sound playing on main thread to avoid weird exceptions
            client.execute(() -> client.world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F, true));
        }

        public static void handleWeaponFailedSoundEvent(MinecraftClient client,
                                                        ClientPlayNetworkHandler handler,
                                                        PacketByteBuf buf,
                                                        PacketSender responseSender) {
            // Get position of sound event
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            // Execute sound playing on main thread to avoid weird exceptions
            client.execute(() -> client.world.playSound(x, y, z, SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, true));
        }

        public static void handlePlayerScoredSoundEvent(MinecraftClient client,
                                                        ClientPlayNetworkHandler handler,
                                                        PacketByteBuf buf,
                                                        PacketSender responseSender) {
            // Execute sound playing on main thread to avoid weird exceptions
            client.execute(() -> client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F));
        }

        public static void handleLasertagGameStarted(MinecraftClient client,
                                                     ClientPlayNetworkHandler handler,
                                                     PacketByteBuf buf,
                                                     PacketSender responseSender) {
            LasertagHudOverlay.progress = 0.0;

            new Thread(() -> {
                for (LasertagHudOverlay.startingIn = 10; LasertagHudOverlay.startingIn >= 0; --LasertagHudOverlay.startingIn) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }).start();
        }

        public static void handleProgress(MinecraftClient client,
                                          ClientPlayNetworkHandler handler,
                                          PacketByteBuf buf,
                                          PacketSender responseSender) {
            LasertagHudOverlay.progress = buf.readDouble();
        }
    }
}
