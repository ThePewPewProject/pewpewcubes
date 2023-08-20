package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.block.LaserTargetBlock;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.util.RaycastUtil;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.networking.ClientNetworkingHandlers;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Class to implement the custom behavior of the lasertag weapon
 *
 * @author Étienne Muser
 */
public class LasertagWeaponItem extends RangedWeaponItem {

    public LasertagWeaponItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        // Laser has no projectiles
        return null;
    }

    @Override
    public int getRange() {
        return Math.toIntExact(LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.WEAPON_REACH));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        // Apply cooldown
        playerEntity.getItemCooldownManager().set(this, Math.toIntExact(LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.WEAPON_COOLDOWN)));

        // Get the item stack
        var laserweaponStack = playerEntity.getStackInHand(hand);

        // Check that player is active
        if (LasertagGameManager.getInstance().getDeactivatedManager().isDeactivated(playerEntity.getUuid())) {
            playWeaponFailSound(playerEntity);
            return TypedActionResult.fail(laserweaponStack);
        }

        // Get the players team
        var team = LasertagGameManager.getInstance().getTeamManager().getTeamOfPlayer(playerEntity.getUuid());

        if (team.isEmpty()) {
            playWeaponFailSound(playerEntity);
            return TypedActionResult.fail(laserweaponStack);
        }

        fireWeapon(world, playerEntity, team.get().color().getValue());
        return TypedActionResult.pass(laserweaponStack);
    }

    private void fireWeapon(World world, PlayerEntity playerEntity, int color) {
        if (!world.isClient) {
            playWeaponFireSound(playerEntity);
        }

        // Raycast the crosshair
        HitResult hit = RaycastUtil.raycastCrosshair(playerEntity, getRange());

        if (world.isClient) {
            switch (hit.getType()) {
                // If a block was hit
                case BLOCK -> {
                    // Cast to BlockHitResult
                    BlockHitResult blockHit = (BlockHitResult) hit;

                    // Get the block pos of the hit block
                    BlockPos blockPos = blockHit.getBlockPos();

                    // Get the hit block
                    BlockState blockState = world.getBlockState(blockPos);
                    net.minecraft.block.Block block = blockState.getBlock();

                    // If hit block is not a lasertarget block
                    if (!(block instanceof LaserTargetBlock laserTarget)) {
                        break;
                    }
                    sendHitLasertarget(playerEntity, blockPos);
                }
                case ENTITY -> {
                    // Cast to EntityHitResult
                    EntityHitResult entityHit = (EntityHitResult) hit;

                    // Get hit entity
                    Entity hitEntity = entityHit.getEntity();

                    // Check that hit entity is a player
                    if (!(hitEntity instanceof ServerPlayerEntity hitPlayer)) {
                        break;
                    }

                    // Cast to player and trigger onHit
                    sendHitPlayer(playerEntity, hitPlayer);
                }
                default -> {
                }
            }
            return;
        }

        // Spawn laser ray entity
        if (LasertagGameManager.getInstance().getSettingsManager().<Boolean>get(SettingDescription.SHOW_LASER_RAYS)) {
            LaserRayEntity ray = new LaserRayEntity(world, playerEntity, color, hit);
            world.spawnEntity(ray);

            // Despawn ray after 50ms
            var despawnThread = ThreadUtil.createScheduledExecutor("lasertag-laserray-despawn-thread-%d");
            despawnThread.schedule(() -> {
                world.getServer().execute(ray::discard);

                ThreadUtil.attemptShutdown(despawnThread);
            }, 50, TimeUnit.MILLISECONDS);
        }
    }

    private static void sendHitLasertarget(PlayerEntity player, BlockPos hitPos) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeUuid(player.getUuid());

        buf.writeDouble(hitPos.getX());
        buf.writeDouble(hitPos.getY());
        buf.writeDouble(hitPos.getZ());

        ClientPlayNetworking.send(NetworkingConstants.PLAYER_HIT_LASERTARGET, buf);
    }

    private static void sendHitPlayer(PlayerEntity player, PlayerEntity target) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeUuid(player.getUuid());
        buf.writeUuid(target.getUuid());

        ClientPlayNetworking.send(NetworkingConstants.PLAYER_HIT_PLAYER, buf);
    }

    private static void playWeaponFailSound(PlayerEntity playerEntity) {
        if (playerEntity.world.isClient) {
            return;
        }

        // Create packet byte buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Put position of sound event into packet
        buf.writeDouble(playerEntity.getX());
        buf.writeDouble(playerEntity.getY());
        buf.writeDouble(playerEntity.getZ());

        ServerEventSending.sendToEveryone((ServerWorld) playerEntity.world, NetworkingConstants.PLAY_WEAPON_FAILED_SOUND, buf);
    }

    private static void playWeaponFireSound(PlayerEntity playerEntity) {
        if (playerEntity.world.isClient) {
            return;
        }

        // Create packet byte buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Put position of sound event into packet
        buf.writeDouble(playerEntity.getX());
        buf.writeDouble(playerEntity.getY());
        buf.writeDouble(playerEntity.getZ());

        ServerEventSending.sendToEveryone((ServerWorld) playerEntity.world, NetworkingConstants.PLAY_WEAPON_FIRED_SOUND, buf);
    }
}
