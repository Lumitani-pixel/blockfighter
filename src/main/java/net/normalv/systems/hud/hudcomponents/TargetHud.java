package net.normalv.systems.hud.hudcomponents;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.render.TargetHudTool;
import net.normalv.util.Util;

import java.awt.*;

public class TargetHud implements Util {
    private final static Identifier hudElement = VanillaHudElements.MISC_OVERLAYS;
    private final static Identifier hudId = Identifier.fromNamespaceAndPath(BlockFighter.MOD_ID, "target_hud");
    private static TargetHudTool targetHudTool;
    public static boolean drawPlayerHead = false;

    private static Entity target = null;

    public static void render(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        if (targetHudTool == null) {
            targetHudTool = BlockFighter.toolManager.getToolByClass(TargetHudTool.class);
            if (targetHudTool == null) return;
        }

        if (!targetHudTool.isEnabled()) return;

        target = BlockFighter.targetManager.getCurrentTarget();
        if(target == null) return;

        drawPlayerHead = targetHudTool.drawPlayerHead();

        int width = mc.font.width(target.getName()) + (drawPlayerHead?50:0) + 20;
        int x = context.guiWidth()/2 - (width/2);
        int y = 10;

        context.fill(x, y, x+width, 40, new Color(100, 100, 200, 150).hashCode());
        context.text(mc.font, target.getName(), drawPlayerHead?x+70: context.guiWidth()/2, y+10, Color.WHITE.hashCode());

        if(target instanceof Player && drawPlayerHead){
            //FIXME idk how this works in the new mappings
//            if(TargetHudTool.getHeadTexture()==null) return;
//            PlayerSkinDrawer.draw(context, TargetHudTool.getHeadTexture(), x+5, y+5, 20);
        }
    }

    public static Identifier getHudElement() {
        return hudElement;
    }

    public static Identifier getHudId() {
        return hudId;
    }
}
