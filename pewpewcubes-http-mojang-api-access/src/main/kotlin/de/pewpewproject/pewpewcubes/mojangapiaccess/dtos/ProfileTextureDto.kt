package de.pewpewproject.pewpewcubes.mojangapiaccess.dtos

data class ProfileTextureDto(
    val timestamp: Long,
    val profileId: String,
    val profileName: String,
    val textures: HashMap<String, TextureObjectDto>
)