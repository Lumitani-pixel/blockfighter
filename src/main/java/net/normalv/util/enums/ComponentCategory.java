package net.normalv.util.enums;

public enum ComponentCategory {
    BOT("Bot"),
    TOOLS("Tools");

    private String name;

    ComponentCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
