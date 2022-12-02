package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.block.LaserTargetBlock;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.lasertaggame.PlayerDeactivatedManager;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.settings.SettingNames;
import de.kleiner3.lasertag.types.ILasertagColorable;
import de.kleiner3.lasertag.util.RaycastUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Class to implement the custom behavior of the lasertag weapon
 *
 * @author Étienne Muser
 */
public class LasertagWeaponItem extends RangedWeaponItem implements ILasertagColorable {

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
        return (int)LasertagSettingsManager.get(SettingNames.WEAPON_REACH);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        // Apply cooldown
        playerEntity.getItemCooldownManager().set(this, (int)LasertagSettingsManager.get(SettingNames.WEAPON_COOLDOWN));

        // Get all armor pieces of the player
        DefaultedList<ItemStack> armorPieces = (DefaultedList<ItemStack>) playerEntity.getArmorItems();

        // Get breastplate of the player
        ItemStack breastplate = armorPieces.get(2);

        // Get the item stack
        var laserweaponStack = playerEntity.getStackInHand(hand);

        // Check that player is active
        if (PlayerDeactivatedManager.isDeactivated(playerEntity.getUuid())) {
            playWeaponFailSound(playerEntity);
            return TypedActionResult.fail(laserweaponStack);
        }

        // Check if player wears vest as breastplate
        if (!(breastplate.getItem() instanceof LasertagVestItem)) {
            playWeaponFailSound(playerEntity);
            return TypedActionResult.fail(laserweaponStack);
        }

        if (!world.isClient) {
            fireWeapon(world, playerEntity, this.getColor(laserweaponStack));
        }
        return TypedActionResult.pass(laserweaponStack);
    }

    public void setDeactivated(ItemStack stack, boolean deactivated) {
        var nbt = stack.getNbt();
        if (nbt == null) {
            nbt = new NbtCompound();
        }
        nbt.putBoolean("deactivated", deactivated);
        stack.setNbt(nbt);
    }

    private void fireWeapon(World world, PlayerEntity playerEntity, int color) {
        playWeaponFireSound(playerEntity);

        // Raycast the crosshair
        HitResult hit = RaycastUtil.raycastCrosshair(playerEntity, getRange());

        switch (hit.getType()) {
            // If a block was hit
            case BLOCK:
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

                // Cast to lasertarget block and trigger onHit
                laserTarget.onHitBy(playerEntity, world.getBlockEntity(blockPos));
                break;

            case ENTITY:
                // Cast to EntityHitResult
                EntityHitResult entityHit = (EntityHitResult) hit;

                // Get hit entity
                Entity hitEntity = entityHit.getEntity();

                // Check that hit entity is a player
                if (!(hitEntity instanceof PlayerEntity hitPlayer))
                    break;

                // Cast to player and trigger onHit
                hitPlayer.onHitBy(playerEntity);
                break;
            case MISS:
                break;
        }

        // Spawn laser ray entity
        if ((boolean)LasertagSettingsManager.get(SettingNames.SHOW_LASER_RAYS)) {
            LaserRayEntity ray = new LaserRayEntity(world, playerEntity, color, hit);
            world.spawnEntity(ray);

            // Despawn ray after 50ms
            Executors.newSingleThreadScheduledExecutor().schedule(() -> world.getServer().execute(ray::discard), 50, TimeUnit.MILLISECONDS);
        }
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
