package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.block.Blocks;
import de.kleiner3.lasertag.block.entity.LasertagFlagBlockEntity;
import de.kleiner3.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;
import de.kleiner3.lasertag.client.screen.ILasertagCreditsScreenOpener;
import de.kleiner3.lasertag.client.screen.ILasertagGameManagerScreenOpener;
import de.kleiner3.lasertag.client.screen.ILasertagTeamSelectorScreenOpener;
import de.kleiner3.lasertag.client.screen.ILasertagTeamZoneGeneratorScreenOpener;
import de.kleiner3.lasertag.lasertaggame.ILasertagPlayer;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the PlayerEntity class to implement the ILasertagPlayer
 *
 * @author Étienne Muser
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ILasertagPlayer, ILasertagGameManagerScreenOpener, ILasertagTeamSelectorScreenOpener, ILasertagCreditsScreenOpener, ILasertagTeamZoneGeneratorScreenOpener {

    @Override
    public void onDeactivated() {

        // Play deactivation sound
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity)(Object)this, NetworkingConstants.PLAY_PLAYER_DEACTIVATED_SOUND);
    }

    @Override
    public void onActivated() {

        // Play activation sound
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity)(Object)this, NetworkingConstants.PLAY_PLAYER_ACTIVATED_SOUND);
    }

    @Override
    public String getLasertagUsername() {
        return ((PlayerEntity)(Object)this).getDisplayName().getString();
    }

    @Override
    public void openLasertagGameManagerScreen(PlayerEntity player) {
        // Do nothing - may not be a client player
    }

    @Override
    public void openLasertagTeamSelectorScreen(PlayerEntity player) {
        // Do nothing - may not be a client player
    }

    @Override
    public void openLasertagCreditsScreen(PlayerEntity player) {
        // Do nothing - may not be a client player
    }

    @Override
    public void openLasertagTeamZoneGeneratorScreen(LasertagTeamZoneGeneratorBlockEntity teamZoneGenerator) {
        // Do nothing - may not be a client player
    }

    /**
     * Inject to allow player to break flag block even tho he is in adventure game mode
     *
     * @param world
     * @param pos
     * @param gameMode
     * @param cir
     */
    @Inject(method = "isBlockBreakingRestricted(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/GameMode;)Z", at = @At("HEAD"), cancellable = true)
    private void isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {

        // Use default logic if player is in spectator or creative game mode
        if (gameMode == GameMode.SPECTATOR ||
            gameMode == GameMode.CREATIVE) {
            return;
        }

        // Get the block state
        var blockState = world.getBlockState(pos);

        // If the block is not a lasertag flag
        if (!blockState.isOf(Blocks.LASERTAG_FLAG_BLOCK)) {

            return;
        }

        // Get the block entity
        var blockEntity = world.getBlockEntity(pos);

        // If is not flag entity
        if (!(blockEntity instanceof LasertagFlagBlockEntity flagEntity)) {
            return;
        }

        // Get the team of the flag
        var flagTeam = LasertagGameManager.getInstance().getTeamManager().getTeamConfigManager()
                .getTeamOfName(flagEntity.getTeamName());

        // If flag has no team
        if (flagTeam.isEmpty()) {
            return;
        }

        // Get the team of the player
        var playerTeam = LasertagGameManager.getInstance().getTeamManager().getTeamOfPlayer(((PlayerEntity)(Object)this).getUuid());

        // If player has no team
        if (playerTeam.isEmpty()) {
            return;
        }

        // If player tries to break his own flag
        if (playerTeam.get().equals(flagTeam.get())) {
            // Player can not break his own flag
            cir.setReturnValue(true);
            return;
        }

        // Get if player is already holding a flag
        var holdingFlag = LasertagGameManager.getInstance().getFlagManager()
                .getPlayerHoldingFlagTeam(((PlayerEntity)(Object)this).getUuid())
                .isPresent();

        // Player is not restricted if he is not holding a flag
        cir.setReturnValue(holdingFlag);
    }
}
