package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.block.Blocks;
import de.pewpewproject.lasertag.block.entity.LasertagFlagBlockEntity;
import de.pewpewproject.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;
import de.pewpewproject.lasertag.client.screen.ILasertagCreditsScreenOpener;
import de.pewpewproject.lasertag.client.screen.ILasertagGameManagerScreenOpener;
import de.pewpewproject.lasertag.client.screen.ILasertagTeamSelectorScreenOpener;
import de.pewpewproject.lasertag.client.screen.ILasertagTeamZoneGeneratorScreenOpener;
import de.pewpewproject.lasertag.lasertaggame.ILasertagPlayer;
import de.pewpewproject.lasertag.networking.NetworkingConstants;
import de.pewpewproject.lasertag.networking.server.ServerEventSending;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity) (Object) this, NetworkingConstants.PLAY_PLAYER_DEACTIVATED_SOUND);
    }

    @Override
    public void onActivated() {

        // Play activation sound
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity) (Object) this, NetworkingConstants.PLAY_PLAYER_ACTIVATED_SOUND);
    }

    @Override
    public String getLasertagUsername() {
        return ((PlayerEntity) (Object) this).getDisplayName().getString();
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
     * Inject to allow player to break flag block even tho he is in adventure game mode.
     * Is basically a copy of isBlockBreakingRestrictedServer.
     *
     * @param world
     * @param pos
     * @param gameMode
     * @param cir
     */
    @Environment(EnvType.CLIENT)
    @Inject(method = "isBlockBreakingRestricted(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/GameMode;)Z", at = @At("HEAD"), cancellable = true)
    private void isBlockBreakingRestrictedClient(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {

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

        boolean holdingFlag = false;

        if (world instanceof ServerWorld serverWorld) {

            // Get the game managers
            var gameManager = serverWorld.getServerLasertagManager();
            var teamsManager = gameManager.getTeamsManager();
            var syncedState = gameManager.getSyncedState();
            var teamsConfigState = syncedState.getTeamsConfigState();
            var captureTheFlagManager = gameManager.getCaptureTheFlagManager();

            // If the pre game count down has not passed
            if (!gameManager.hasPreGamePassed()) {
                // Player can not break flags before the pre game count down has passed
                cir.setReturnValue(true);
                return;
            }

            // Get the team of the flag
            var flagTeam = teamsConfigState.getTeamOfName(flagEntity.getTeamName());

            // If flag has no team
            if (flagTeam.isEmpty()) {
                return;
            }

            // Get the team of the player
            var playerTeam = teamsManager.getTeamOfPlayer(((PlayerEntity) (Object) this).getUuid());

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
            holdingFlag = captureTheFlagManager
                    .getPlayerHoldingFlagTeam(((PlayerEntity) (Object) this).getUuid())
                    .isPresent();
        } else if (world instanceof ClientWorld clientWorld) {

            // Get the game managers
            var gameManager = clientWorld.getClientLasertagManager();
            var teamsManager = gameManager.getTeamsManager();
            var syncedState = gameManager.getSyncedState();
            var teamsConfigState = syncedState.getTeamsConfigState();
            var captureTheFlagManager = gameManager.getCaptureTheFlagManager();
            var uiManager = gameManager.getUIStateManager();

            // If the pre game count down has not passed
            if (!uiManager.hasPreGamePassed()) {
                // Player can not break flags before the pre game count down has passed
                cir.setReturnValue(true);
                return;
            }

            // Get the team of the flag
            var flagTeam = teamsConfigState.getTeamOfName(flagEntity.getTeamName());

            // If flag has no team
            if (flagTeam.isEmpty()) {
                return;
            }

            // Get the team of the player
            var playerTeam = teamsManager.getTeamOfPlayer(((PlayerEntity) (Object) this).getUuid())
                    .map(teamId -> teamsConfigState.getTeamOfId(teamId).orElseThrow());

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
            holdingFlag = captureTheFlagManager
                    .getPlayerHoldingFlagTeam(((PlayerEntity) (Object) this).getUuid())
                    .isPresent();
        }

        // Player is not restricted if he is not holding a flag
        cir.setReturnValue(holdingFlag);
    }

    /**
     * Inject to allow player to break flag block even tho he is in adventure game mode.
     * Is basically a duplicate of isBlockBreakingRestrictedClient because on the server
     * the class MinecraftClient does not exist.
     *
     * @param world
     * @param pos
     * @param gameMode
     * @param cir
     */
    @Environment(EnvType.SERVER)
    @Inject(method = "isBlockBreakingRestricted(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/GameMode;)Z", at = @At("HEAD"), cancellable = true)
    private void isBlockBreakingRestrictedServer(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {

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

        boolean holdingFlag = false;

        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        // Get the game managers
        var gameManager = serverWorld.getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();
        var captureTheFlagManager = gameManager.getCaptureTheFlagManager();

        // If the pre game count down has not passed
        if (!gameManager.hasPreGamePassed()) {
            // Player can not break flags before the pre game count down has passed
            cir.setReturnValue(true);
            return;
        }

        // Get the team of the flag
        var flagTeam = teamsConfigState.getTeamOfName(flagEntity.getTeamName());

        // If flag has no team
        if (flagTeam.isEmpty()) {
            return;
        }

        // Get the team of the player
        var playerTeam = teamsManager.getTeamOfPlayer(((PlayerEntity) (Object) this).getUuid());

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
        holdingFlag = captureTheFlagManager
                .getPlayerHoldingFlagTeam(((PlayerEntity) (Object) this).getUuid())
                .isPresent();

        // Player is not restricted if he is not holding a flag
        cir.setReturnValue(holdingFlag);
    }
}
