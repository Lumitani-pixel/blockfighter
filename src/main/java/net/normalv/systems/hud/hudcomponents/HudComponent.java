package net.normalv.systems.hud.hudcomponents;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.normalv.util.interfaces.Util;

public class HudComponent implements Util {
    private static Identifier hudElement;
    private static Identifier identifier;

    public HudComponent(Identifier hudElement, Identifier identifier) {
        HudComponent.hudElement = hudElement;
        HudComponent.identifier = identifier;
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter){
    }

    public static Identifier getHudElement() {
        return hudElement;
    }

    public static Identifier getIdentifier() {
        return identifier;
    }
}
