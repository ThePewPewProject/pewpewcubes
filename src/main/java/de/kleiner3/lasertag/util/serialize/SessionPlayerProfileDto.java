package de.kleiner3.lasertag.util.serialize;

/**
 * Dto for the result coming from the sessionserver of mojang for the session profile request
 *
 * @author Étienne Muser
 */
public class SessionPlayerProfileDto {
    public String id;
    public String name;
    public PropertyDto[] properties;

    public static class PropertyDto {
        public String name;
        public String value;
    }
}
