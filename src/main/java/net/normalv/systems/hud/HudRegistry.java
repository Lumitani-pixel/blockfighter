package net.normalv.systems.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.normalv.BlockFighter;
import net.normalv.systems.hud.hudcomponents.TargetHud;
import net.normalv.systems.hud.hudcomponents.ToolHud;

public class HudRegistry {
    public HudRegistry() {
        new ToolHud();
        new TargetHud();
    }

    public void register() {
        BlockFighter.LOGGER.info("HudRegistry.register() called");
        HudElementRegistry.attachElementBefore(ToolHud.getHudElement(), ToolHud.getHudId(), ToolHud::render);
        HudElementRegistry.attachElementBefore(TargetHud.getHudElement(), TargetHud.getHudId(), TargetHud::render);
    }
}