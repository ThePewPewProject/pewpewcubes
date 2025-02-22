package de.pewpewproject.pewpewcubes.mojangapiaccess.dtos

data class PlayerSessionProfileDto(
    val name: String,
    val id: String,
    val properties: Array<PlayerSessionPropertyDto>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerSessionProfileDto

        if (name != other.name) return false
        if (id != other.id) return false
        if (!properties.contentEquals(other.properties)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + properties.contentHashCode()
        return result
    }
}