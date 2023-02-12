package de.kleiner3.lasertag.types;

import java.util.Objects;

/**
 * Color DTO
 *
 * @author Étienne Muser
 */
public class ColorDto {
    public ColorDto(int r, int g, int b) {
        this.r = (r & 0xFF);
        this.g = (g & 0xFF);
        this.b = (b & 0xFF);
    }

    private final int r;
    private final int g;
    private final int b;

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    /**
     * Get the integer color value with bits distributed like:
     * RRRRRRRRGGGGGGGGBBBBBBBB
     * @return The int value of this color
     */
    public int getValue() {
        return this.r << 16 | this.g << 8 | this.b;
    }

    @Override
    public String toString() {
        return "(" + r + ", " + g + ", " + b + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        ColorDto colorDto = (ColorDto) o;

        return r == colorDto.r && g == colorDto.g && b == colorDto.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
