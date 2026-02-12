package net.normalv.systems.hud.hudcomponents;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.render.TargetHudTool;
import net.normalv.util.Util;

import java.awt.*;

public class TargetHud implements Util {
    private final static Identifier hudElement = VanillaHudElements.MISC_OVERLAYS;
    private final static Identifier hudId = Identifier.of(BlockFighter.MOD_ID, "target_hud");
    private static TargetHudTool targetHudTool;
    public static boolean drawPlayerHead = false;

    private static Entity target = null;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (targetHudTool == null) {
            targetHudTool = BlockFighter.toolManager.getToolByClass(TargetHudTool.class);
            if (targetHudTool == null) return;
        }

        if (!targetHudTool.isEnabled()) return;

        target = BlockFighter.targetManager.getCurrentTarget();
        if(target == null) return;

        drawPlayerHead = targetHudTool.drawPlayerHead();

        int width = mc.textRenderer.getWidth(target.getName()) + (drawPlayerHead?50:0) + 20;
        int x = context.getScaledWindowWidth()/2 - (width/2);
        int y = 10;

        context.fill(x, y, x+width, 40, new Color(100, 100, 200, 150).hashCode());
        context.drawCenteredTextWithShadow(mc.textRenderer, target.getName(), drawPlayerHead?x+70: context.getScaledWindowWidth()/2, y+10, Color.WHITE.hashCode());

        if(target instanceof PlayerEntity && drawPlayerHead){
            if(TargetHudTool.getHeadTexture()==null) return;
            PlayerSkinDrawer.draw(context, TargetHudTool.getHeadTexture(), x+5, y+5, 20);
        }
    }

    public static Identifier getHudElement() {
        return hudElement;
    }

    public static Identifier getHudId() {
        return hudId;
    }
}
