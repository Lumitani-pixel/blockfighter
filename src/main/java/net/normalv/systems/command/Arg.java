package net.normalv.systems.command;

public class Arg<T> {
    private final String name;
    private final Class<T> type;
    private final T defaultValue;

    public Arg(String name, Class<T> type) {
        this(name, type, null);
    }

    public Arg(String name, Class<T> type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() { return name; }
    public Class<T> getType() { return type; }
    public T getDefaultValue() { return defaultValue; }

    public T parse(String input) throws IllegalArgumentException {
        if (type == String.class) return (T) input;
        if (type == Integer.class) return (T) Integer.valueOf(input);
        if (type == Double.class) return (T) Double.valueOf(input);
        if (type == Float.class) return (T) Float.valueOf(input);
        if (type == Boolean.class) return (T) Boolean.valueOf(input);
        throw new IllegalArgumentException("Unsupported arg type: " + type.getSimpleName());
    }
}