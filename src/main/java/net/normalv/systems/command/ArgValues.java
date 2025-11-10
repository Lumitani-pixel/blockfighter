package net.normalv.systems.command;

import java.util.HashMap;
import java.util.Map;

public class ArgValues {
    private final Map<String, Object> values = new HashMap<>();

    public <T> void put(String name, T value) {
        values.put(name, value);
    }

    public <T> T get(String name) {
        return (T) values.get(name);
    }

    public boolean has(String name) {
        return values.containsKey(name);
    }
}