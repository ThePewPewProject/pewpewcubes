package de.kleiner3.lasertag.common.types;

/**
 * Class representing a vector of 3 generic elements
 *
 * @author Étienne Muser
 * @param x The first element
 * @param y The second element
 * @param z The third element
 * @param <X> The type of the first element
 * @param <Y> The type of the second element
 * @param <Z> The type of the third element
 */
public record Vec3<X, Y, Z> (X x, Y y, Z z) {

}
