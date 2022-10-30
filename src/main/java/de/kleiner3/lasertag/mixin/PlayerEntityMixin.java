package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.item.LasertagVestItem;
import de.kleiner3.lasertag.lasertaggame.ILasertagPlayer;
import de.kleiner3.lasertag.lasertaggame.PlayerDeactivatedManager;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin into the PlayerEntity class to implement the ILasertagPlayer
 *
 * @author Étienne Muser
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ILasertagPlayer {

    /**
     * The players lasertag score
     */
    private int score = 0;
    private Colors.Color team = null;

    @Override
    public int getLasertagScore() {
        return score;
    }

    @Override
    public void resetLasertagScore() {
        score = 0;
    }

    @Override
    public void increaseScore(int score) {
        this.score += score;
    }

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
        if (PlayerDeactivatedManager.isDeactivated(((PlayerEntity)(Object)this).getUuid())) {
            return;
        }

        // Deactivate player
        PlayerDeactivatedManager.deactivate(((PlayerEntity) (Object) this), player.getWorld());

        // Get the server
        MinecraftServer server = player.getServer();
        if (server != null) {
            server.onPlayerScored(player, LasertagConfig.getInstance().getPlayerHitScore());
            ServerEventSending.sendPlayerScoredSoundEvent((ServerPlayerEntity) player);
        }
    }

    @Override
    public void setTeam(Colors.Color team) {
        this.team = team;
    }

    @Override
    public Colors.Color getTeam() {
        return this.team;
    }

    @Override
    public void onDeactivated() {
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
}
