package de.kleiner3.lasertag.lasertaggame.statistics.mojangsessionaccess;

/**
 * Dto for the result coming from the sessionserver of mojang for the session profile request
 *
 * @author Étienne Muser
 */
public record SessionPlayerProfileDto(String id,
                                      String name,
                                      PropertyDto[] properties) {
}
