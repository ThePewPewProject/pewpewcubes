package de.pewpewproject.pewpewcubes.entities.settings

/**
 * The possible data types for settings
 * @author Étienne Muser
 */
class SettingDataType private constructor(val valueType: Class<*>?, val isEnum: Boolean = false) {
    @Deprecated("This constructor is only defined to hide the default constructor. DO NOT USE!")
    private constructor() : this(null, false)

    companion object {
        val LONG: SettingDataType = SettingDataType(Long::class.java)
        val BOOL: SettingDataType = SettingDataType(Boolean::class.java)

        fun ofEnum(enumType: Class<out Enum<*>?>?): SettingDataType {
            return SettingDataType(enumType, true)
        }
    }
}