package de.kleiner3.lasertag.common.types;

/**
 * Util class implementing a tuple. For some reason minecrafts internal Pair class doesn't work with networking and Gson.
 *
 * @param <A> The type of the left element
 * @param <B> The type of the right element
 * @author Étienne Muser
 */
public record Tuple<A, B>(A x, B y) {
}
