package de.pewpewproject.lasertag.common.types;

import java.util.Objects;

/**
 * Color DTO
 *
 * @author Étienne Muser
 */
public class ColorDto {
    private int r;
    private int g;
    private int b;

    public ColorDto(int r, int g, int b) {
        this.r = (r & 0xFF);
        this.g = (g & 0xFF);
        this.b = (b & 0xFF);
    }

    /**
     * Get the integer color value with bits distributed like:
     * RRRRRRRRGGGGGGGGBBBBBBBB
     *
     * @return The int value of this color
     */
    public int getValue() {
        return this.r << 16 | this.g << 8 | this.b;
    }

    public int r() {
        return r;
    }

    public int g() {
        return g;
    }

    public int b() {
        return b;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ColorDto otherColor) {
            return r == otherColor.r &&
                    g == otherColor.g &&
                    b == otherColor.b;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }

    @Override
    public String toString() {
        return "[" + r + ", " + g + ", " + b + "]";
    }
}
