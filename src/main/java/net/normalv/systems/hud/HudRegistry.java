package net.normalv.systems.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.normalv.systems.hud.hudcomponents.ToolHud;

public class HudRegistry {
    public HudRegistry() {
        new ToolHud();
    }

    public void register() {
        HudElementRegistry.attachElementBefore(ToolHud.getHudElement(), ToolHud.getIdentifier(), ToolHud::render);
    }
}