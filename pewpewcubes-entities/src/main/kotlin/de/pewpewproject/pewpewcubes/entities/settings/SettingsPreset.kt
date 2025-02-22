package de.pewpewproject.pewpewcubes.entities.settings


data class SettingsPreset(
    val name: String,
    val gameMode: String,
    val settings: HashMap<String, Any>
)