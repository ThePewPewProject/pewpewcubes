package de.pewpewproject.lasertag.lasertaggame.statistics.mojangsessionaccess;

/**
 * Dto for the result coming from the sessionserver of mojang for the session profile request
 *
 * @author Étienne Muser
 */
public class SessionPlayerProfileDto {
    public String id;
    public String name;
    public PropertyDto[] properties;
}
