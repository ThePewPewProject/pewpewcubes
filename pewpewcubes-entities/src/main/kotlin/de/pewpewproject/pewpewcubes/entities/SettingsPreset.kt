package de.pewpewproject.pewpewcubes.entities


data class SettingsPreset(
    val name: String,
    val gameMode: String,
    val settings: HashMap<String, Any>
)