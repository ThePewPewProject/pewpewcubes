package de.pewpewproject.lasertag.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Class to hold all damage sources
 *
 * @author Étienne Muser
 */
public class DamageSources {

    public static DamageSource laser(PlayerEntity shooter) {

        return new EntityDamageSource("laser", shooter).setBypassesArmor().setUnblockable();
    }
}
