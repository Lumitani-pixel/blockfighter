package net.normalv.systems.hud.hudcomponents;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.normalv.BlockFighter;

import java.awt.*;

public class TestingHud extends HudComponent{

    public TestingHud() {
        super(VanillaHudElements.CHAT, Identifier.of(BlockFighter.MOD_ID, "before_chat"));
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter){
        context.fill(10, 10, 50, 50, Color.RED.hashCode());
    }
}