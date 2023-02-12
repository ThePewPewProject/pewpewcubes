package de.kleiner3.lasertag.lasertaggame.statistics.mojangsessionaccess;

/**
 * Dto for the result coming from the mojang api for the user profile request
 *
 * @author Étienne Muser
 */
public record PlayerInfoDto(String name, String id) {
}
