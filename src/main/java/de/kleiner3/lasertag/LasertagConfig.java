package de.kleiner3.lasertag;

/**
 * All configuration variables for lasertag.
 *
 * @author Étienne Muser
 */
public class LasertagConfig {
    // ===== Weapon settings ====================
    public static int lasertagWeaponCooldown = 2;
    public static int lasertagWeaponReach = 50;
    public static boolean showLaserRays = true;

    // ===== General game settings ==============
    public static int maxTeamSize = 6;
    public static boolean renderTeamList = true;
    public static boolean renderTimer = true;
    public static int lasertargetHitScore = 100;
    public static int playerHitScore = 20;
}
