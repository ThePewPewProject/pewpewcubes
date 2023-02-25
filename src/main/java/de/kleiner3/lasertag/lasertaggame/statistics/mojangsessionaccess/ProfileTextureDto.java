package de.kleiner3.lasertag.lasertaggame.statistics.mojangsessionaccess;

import java.util.HashMap;

/**
 * Dto for the profile texture coming from the session server of mojang
 *
 * @author Étienne Muser
 */
public class ProfileTextureDto {
    public long timestamp;
    public String profileId;
    public String profileName;
    public HashMap<String, TextureObjectDto> textures;
}
