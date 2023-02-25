package de.kleiner3.lasertag.common.types;

import java.util.Objects;

/**
 * Util class implementing a tuple. For some reason minecrafts internal Pair class doesn't work with networking and Gson.
 *
 * @param <A> The type of the left element
 * @param <B> The type of the right element
 * @author Étienne Muser
 */
public class Tuple<A, B> {
    private A x;
    private B y;

    public Tuple(A x, B y) {
        this.x = x;
        this.y = y;
    }

    public A x() {
        return x;
    }

    public B y() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Tuple<?,?> otherTuple) {
            return x.equals(otherTuple.x) &&
                    y.equals(otherTuple.y);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x.toString() + ", " + y.toString() + ")";
    }
}
