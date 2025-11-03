package net.normalv.systems.tools.setting;

public class Setting <T>{
    private T value;
    private T minValue;
    private T maxValue;
    private T defaultValue;
    private boolean hasRestrictions;
    private String description;
    private String name;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.description = "";
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public Setting(String name, T defaultValue, T minValue, T maxValue) {
        this.name = name;
        this.description = "";
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.hasRestrictions = true;
    }

    public Setting(String name, String description, T defaultValue, T minValue, T maxValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.hasRestrictions = true;
    }

    public void setValue(T value) {
        if(!hasRestrictions){
            this.value = value;
            return;
        }
        if(((Number) value).floatValue() < ((Number) minValue).floatValue()) this.value = minValue;
        else if(((Number) value).floatValue() > ((Number) maxValue).floatValue()) this.value = maxValue;
        else this.value = value;
    }

    public boolean isNumberSetting() {
        return this.value instanceof Double || this.value instanceof Integer || this.value instanceof Short || this.value instanceof Long || this.value instanceof Float;
    }

    public boolean isBooleanSetting() {
        return this.value instanceof Boolean;
    }

    public boolean isEnumSetting() {
        return !this.isNumberSetting() && !(this.value instanceof String) && !(this.value instanceof Character) && !(this.value instanceof Boolean);
    }

    public boolean isStringSetting() {
        return this.value instanceof String;
    }

    public T getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
