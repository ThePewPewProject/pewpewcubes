package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.ILasertagPlayer;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin into the PlayerEntity class to implement the ILasertagPlayer
 *
 * @author Étienne Muser
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ILasertagPlayer {

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
}
