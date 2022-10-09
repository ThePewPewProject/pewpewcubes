package de.kleiner3.lasertag.item;

import java.util.function.Predicate;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.Types.Colors;
import de.kleiner3.lasertag.Util.RaycastUtil;
import de.kleiner3.lasertag.block.LaserTargetBlock;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LasertagWeaponItem extends RangedWeaponItem {
	private Colors color;

	public LasertagWeaponItem(Settings settings, Colors color) {
		super(settings);
		this.color = color;
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		// Laser has no projectiles
		return null;
	}

	@Override
	public int getRange() {
		return LasertagConfig.lasertagWeaponReach;
	}

	public Colors getColor() {
		return color;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		// Apply cooldown
		playerEntity.getItemCooldownManager().set(this, LasertagConfig.lasertagWeaponCooldown);

		// Get all armor pieces of the player
		DefaultedList<ItemStack> armorPieces = (DefaultedList<ItemStack>) playerEntity.getArmorItems();

		// Get breastplate of the player
		ItemStack breastplate = armorPieces.get(2);

		// Check if player wears vest as breastplate
		if (!(breastplate.getItem() instanceof LasertagVestItem)) {
			playWeaponFailSound(playerEntity);
			return TypedActionResult.fail(playerEntity.getStackInHand(hand));
		}

		// Check if vest is of same color as weapon
		if (!(((LasertagVestItem) breastplate.getItem()).getColor() == this.color)) {
			playWeaponFailSound(playerEntity);
			return TypedActionResult.fail(playerEntity.getStackInHand(hand));
		}

		fireWeapon(world, playerEntity, hand);
		return TypedActionResult.success(playerEntity.getStackInHand(hand));
	}

	private void fireWeapon(World world, PlayerEntity playerEntity, Hand hand) {
		// TODO: Play weapon fire sound
		playerEntity.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);

		// ===== Raycast the crosshair
		HitResult hit = RaycastUtil.raycastCrosshair(playerEntity, LasertagConfig.lasertagWeaponReach);

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
			if (!(block instanceof LaserTargetBlock)) {
				break;
			}

			// Cast to lasertarget block and trigger onHit
			LaserTargetBlock laserTarget = (LaserTargetBlock) block;
			laserTarget.onHitBy(playerEntity);
			break;

		case ENTITY:
			// Cast to EntityHitResult
			EntityHitResult entityHit = (EntityHitResult) hit;
			break;
		case MISS:

			break;
		}

		// Spawn laser ray entity
		if (LasertagConfig.showLaserRays) {
			LaserRayEntity ray = new LaserRayEntity(world, playerEntity, color, hit);
			world.spawnEntity(ray);

			new Thread(() -> {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				ray.discard();
			}).start();
		}
	}

	private void playWeaponFailSound(PlayerEntity playerEntity) {
		// TODO: Play weapon failed sound
		playerEntity.playSound(SoundEvents.BLOCK_BAMBOO_BREAK, 1.0F, 1.0F);
	}
}
