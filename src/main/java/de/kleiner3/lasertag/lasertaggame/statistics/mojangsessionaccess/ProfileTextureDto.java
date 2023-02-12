package de.kleiner3.lasertag.lasertaggame.statistics.mojangsessionaccess;

import java.util.HashMap;

/**
 * Dto for the profile texture coming from the session server of mojang
 *
 * @author Étienne Muser
 */
public record ProfileTextureDto(long timestamp,
                                String profileId,
                                String profileName,
                                HashMap<String, TextureObjectDto> textures) {
}
