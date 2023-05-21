package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.item.LasertagVestItem;
import de.kleiner3.lasertag.lasertaggame.ILasertagPlayer;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin into the PlayerEntity class to implement the ILasertagPlayer
 *
 * @author Étienne Muser
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ILasertagPlayer {
    private TeamDto team = null;

    @Override
    public void onHitBy(PlayerEntity player) {
        // Get this players chestplate
        DefaultedList<ItemStack> armor = (DefaultedList<ItemStack>) ((PlayerEntity) (Object) this).getArmorItems();
        Item chestplate = armor.get(2).getItem();

        // check that hit players chestplate is lasertag vest
        if (!(chestplate instanceof LasertagVestItem)) {
            return;
        }

        // Get firing players chestplate
        DefaultedList<ItemStack> firedArmor = (DefaultedList<ItemStack>) player.getArmorItems();
        Item firedChestplate = firedArmor.get(2).getItem();

        // Check that firiedChestplate is lasertag vest
        if (!(firedChestplate instanceof LasertagVestItem)) {
            return;
        }

        // Check that hit player is not in same team as firing player
        if (team.equals(player.getTeam())) {
            return;
        }

        // Check that hit player is not deactivated
        if (LasertagGameManager.getInstance().getDeactivatedManager().isDeactivated(((PlayerEntity)(Object)this).getUuid())) {
            return;
        }

        // Deactivate player
        LasertagGameManager.getInstance().getDeactivatedManager().deactivate(((PlayerEntity) (Object) this), player.getWorld());

        // Get the server
        MinecraftServer server = player.getServer();
        if (server != null) {
            server.onPlayerScored(player, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAYER_HIT_SCORE));
            ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity) player, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND);
        }
    }

    @Override
    public void setTeam(TeamDto team) {
        this.team = team;
    }

    @Override
    public TeamDto getTeam() {
        return this.team;
    }

    @Override
    public void onDeactivated() {
        // Play deactivation sound
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity)(Object)this, NetworkingConstants.PLAY_PLAYER_DEACTIVATED_SOUND);

        // Get players weapon
        var weaponStack = ((PlayerEntity) (Object) this).getMainHandStack();

        // Set weapon nbt deactivated
        var nbt = weaponStack.getNbt();
        if (nbt == null) {
            nbt = new NbtCompound();
        }
        nbt.putBoolean("deactivated", true);
        weaponStack.setNbt(nbt);
    }

    @Override
    public void onActivated() {
        // Play activation sound
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity)(Object)this, NetworkingConstants.PLAY_PLAYER_ACTIVATED_SOUND);

        // Get players weapon
        var weaponStack = ((PlayerEntity) (Object) this).getMainHandStack();

        // Set weapon nbt deactivated
        var nbt = weaponStack.getNbt();
        if (nbt == null) {
            nbt = new NbtCompound();
        }
        nbt.putBoolean("deactivated", false);
        weaponStack.setNbt(nbt);
    }

    @Override
    public String getLasertagUsername() {
        return ((PlayerEntity)(Object)this).getDisplayName().getString();
    }
}
