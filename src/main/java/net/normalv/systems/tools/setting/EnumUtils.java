package net.normalv.systems.tools.setting;

public class EnumUtils {

    public int getIndexOfEnum(Enum<?> enumClass) {
        for(int i = 0; i<enumClass.getDeclaringClass().getEnumConstants().length; i++) {
            if(!enumClass.getDeclaringClass().getEnumConstants()[i].name().equalsIgnoreCase(enumClass.name())) continue;
            return i;
        }
        return -1;
    }

    public Enum<?> getNextEnum(Enum<?> enumClass) {
        return enumClass.getDeclaringClass().getEnumConstants()
                [getIndexOfEnum(enumClass)+1 >= enumClass.getDeclaringClass().getEnumConstants().length ? 0 : getIndexOfEnum(enumClass)+1];
    }

    public String getFormatedName(Enum<?> enumClass) {
        return Character.toUpperCase(enumClass.name().charAt(0)) + enumClass.name().substring(1).toLowerCase();
    }
}
