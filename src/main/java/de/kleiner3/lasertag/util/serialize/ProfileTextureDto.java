package de.kleiner3.lasertag.util.serialize;

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
    public HashMap<String, TextureObject> textures;

    public class TextureObject {
        public String url;
    }
}
