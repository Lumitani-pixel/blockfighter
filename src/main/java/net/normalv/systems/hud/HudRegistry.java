package net.normalv.systems.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.normalv.systems.hud.hudcomponents.TestingHud;

public class HudRegistry {
    public HudRegistry() {
        new TestingHud();
    }

    public void register() {
        HudElementRegistry.attachElementBefore(TestingHud.getHudElement(), TestingHud.getIdentifier(), TestingHud::render);
    }
}