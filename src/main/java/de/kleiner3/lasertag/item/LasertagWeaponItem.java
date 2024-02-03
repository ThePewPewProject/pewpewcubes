package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.LaserTargetBlock;
import de.kleiner3.lasertag.client.SoundEvents;
import de.kleiner3.lasertag.common.util.RaycastUtil;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Class to implement the custom behavior of the lasertag weapon
 *
 * @author Étienne Muser
 */
public class LasertagWeaponItem extends RangedWeaponItem implements IAnimatable {

    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

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
        return 0;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {

        LasertagMod.LOGGER.info("Player '" + playerEntity.getDisplayName().getString() + "' is using the weapon.");

        if (world.isClient) {
            return useClient(world, playerEntity, hand);
        } else {
            return useServer((ServerWorld)world, (ServerPlayerEntity)playerEntity, hand);
        }
    }


    private TypedActionResult<ItemStack> useServer(ServerWorld world, ServerPlayerEntity playerEntity, Hand hand) {

        // Get the game managers
        var gameManager = world.getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();
        var activationManager = gameManager.getActivationManager();
        var teamsManager = gameManager.getTeamsManager();

        // Apply cooldown
        playerEntity.getItemCooldownManager().set(this, Math.toIntExact(settingsManager.<Long>get(SettingDescription.WEAPON_COOLDOWN)));

        // Get the item stack
        var laserweaponStack = playerEntity.getStackInHand(hand);

        // Check that player is active
        if (activationManager.isDeactivated(playerEntity.getUuid())) {
            LasertagMod.LOGGER.info("[Server, " + playerEntity.getDisplayName().getString() + "] Weapon fail. Deactivated.");
            world.playSound(null, playerEntity.getBlockPos(), net.minecraft.sound.SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return TypedActionResult.fail(laserweaponStack);
        }

        // Get the players team
        var team = teamsManager.getTeamOfPlayer(playerEntity.getUuid());

        if (team.isEmpty()) {
            LasertagMod.LOGGER.info("[Server, " + playerEntity.getDisplayName().getString() + "] Weapon fail. Not in team.");
            world.playSound(null, playerEntity.getBlockPos(), net.minecraft.sound.SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return TypedActionResult.fail(laserweaponStack);
        }

        fireWeaponServer(world, playerEntity, team.get().color().getValue());
        return TypedActionResult.pass(laserweaponStack);
    }

    @Environment(EnvType.CLIENT)
    private TypedActionResult<ItemStack> useClient(World world, PlayerEntity playerEntity, Hand hand) {

        // Cast
        var clientWorld = (ClientWorld)world;

        // Get the game managers
        var gameManager = clientWorld.getClientLasertagManager();
        var settingsManager = gameManager.getSettingsManager();
        var activationManager = gameManager.getActivationManager();
        var teamsManager = gameManager.getTeamsManager();
        var teamsConfigState = gameManager.getSyncedState().getTeamsConfigState();

        // Apply cooldown
        playerEntity.getItemCooldownManager().set(this, Math.toIntExact(settingsManager.<Long>get(SettingDescription.WEAPON_COOLDOWN)));

        // Get the item stack
        var laserweaponStack = playerEntity.getStackInHand(hand);

        // Check that player is active
        if (activationManager.isDeactivated(playerEntity.getUuid())) {
            LasertagMod.LOGGER.info("[Client, " + playerEntity.getDisplayName().getString() + "] Weapon fail. Deactivated.");
            return TypedActionResult.fail(laserweaponStack);
        }

        // Get the players team
        var team = teamsManager.getTeamOfPlayer(playerEntity.getUuid());

        if (team.isEmpty()) {
            LasertagMod.LOGGER.info("[Client, " + playerEntity.getDisplayName().getString() + "] Weapon fail. Not in team.");
            return TypedActionResult.fail(laserweaponStack);
        }

        fireWeaponClient(clientWorld, playerEntity);
        return TypedActionResult.pass(laserweaponStack);
    }

    private void fireWeaponServer(ServerWorld world, ServerPlayerEntity player, int color) {

        // Get the game managers
        var gameManager = world.getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        world.playSound(null, player.getBlockPos(), SoundEvents.LASERWEAPON_FIRE_SOUND_EVENT, SoundCategory.PLAYERS, 0.8f, 1.0f);

        // Raycast the crosshair
        HitResult hit = RaycastUtil.raycastCrosshair(player, Math.toIntExact(settingsManager.<Long>get(SettingDescription.WEAPON_REACH)));

        // Spawn laser ray entity
        if (settingsManager.<Boolean>get(SettingDescription.SHOW_LASER_RAYS)) {
            LaserRayEntity ray = new LaserRayEntity(world, player, color, hit);
            world.spawnEntity(ray);

            // Despawn ray after 50ms
            var despawnThread = ThreadUtil.createScheduledExecutor("server-lasertag-laserray-despawn-thread-%d");
            despawnThread.schedule(() -> {
                world.getServer().execute(ray::discard);

                despawnThread.shutdownNow();
            }, 50, TimeUnit.MILLISECONDS);
        }
    }

    @Environment(EnvType.CLIENT)
    private void fireWeaponClient(ClientWorld world, PlayerEntity playerEntity) {

        // Get the game managers
        var gameManager = world.getClientLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        // Raycast the crosshair
        HitResult hit = RaycastUtil.raycastCrosshair(playerEntity, Math.toIntExact(settingsManager.<Long>get(SettingDescription.WEAPON_REACH)));

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
                if (!(hitEntity instanceof PlayerEntity hitPlayer)) {
                    break;
                }

                // Cast to player and trigger onHit
                sendHitPlayer(playerEntity, hitPlayer);
            }
            default -> {
                LasertagMod.LOGGER.info("[Client, " + playerEntity.getDisplayName().getString() + "] Nothing hit.");
            }
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

    private PlayState predicate(AnimationEvent<LasertagWeaponItem> event) {
        // No animation

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
