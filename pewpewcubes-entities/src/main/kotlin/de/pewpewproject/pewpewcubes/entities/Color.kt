package de.pewpewproject.pewpewcubes.entities

/**
 * Class representing an RGB color
 * @author Étienne Muser
 */
data class Color(val red: UByte, val green: UByte, val blue: UByte) {
    /**
     * Get the integer color value with bits distributed like:
     * RRRRRRRRGGGGGGGGBBBBBBBB
     *
     * @return The int value of this color
     */
    fun getValue(): Int {
        return (this.red.toInt() shl 16) or
               (this.green.toInt() shl 8) or
                this.blue.toInt()
    }
}