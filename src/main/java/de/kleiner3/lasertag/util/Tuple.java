package de.kleiner3.lasertag.util;

/**
 * Util class implementing a tuple. For some reason minecrafts internal Pair class doesn't work with networking and Gson.
 *
 * @param <A> The type of the left element
 * @param <B> The type of the right element
 * @author Étienne Muser
 */
public class Tuple<A, B> {
    public A x;
    public B y;

    public Tuple(A x, B y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple other)) {
            return false;
        }

        return x.equals(other.x) && y.equals(other.y);
    }
}
