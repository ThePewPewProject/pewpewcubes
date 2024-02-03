package de.kleiner3.lasertag.lasertaggame.settings;

/**
 * @author Étienne Muser
 */
public class SettingDataType {
    public static final SettingDataType LONG = new SettingDataType(Long.class);
    public static final SettingDataType BOOL = new SettingDataType(Boolean.class);

    private final Class<?> valueType;

    private final boolean isEnum;

    /**
     * This constructor is only defined to hide the default constructor. DO NOT USE!
     */
    @Deprecated
    private SettingDataType() {
        this(null, false);
    }

    private SettingDataType(Class<?> valueType) {
        this(valueType, false);
    }

    private SettingDataType(Class<?> valueType, boolean isEnum) {
        this.valueType = valueType;
        this.isEnum = isEnum;
    }

    public Class<?> getValueType() {
        return this.valueType;
    }

    public boolean isEnum() {
        return this.isEnum;
    }

    public static SettingDataType ofEnum(Class<? extends Enum<?>> enumType) {
        return new SettingDataType(enumType, true);
    }
}
