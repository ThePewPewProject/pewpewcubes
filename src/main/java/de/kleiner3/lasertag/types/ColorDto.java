package de.kleiner3.lasertag.types;

/**
 * Color DTO
 *
 * @author Étienne Muser
 */
public record ColorDto(int r, int g, int b) {
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
}
