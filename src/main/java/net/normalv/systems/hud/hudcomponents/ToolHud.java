package net.normalv.systems.hud.hudcomponents;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.color.ColorLerper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.client.HudTool;
import net.normalv.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolHud implements Util {
    private static final Identifier hudElement = VanillaHudElements.MISC_OVERLAYS;
    private static final Identifier hudId = Identifier.fromNamespaceAndPath(BlockFighter.MOD_ID, "tool_hud");

    private static float colorCycleSpeed = 0.3f;
    private static boolean rainbow = false;
    private static final int TOOL_SPACING = 10;

    private static long lastUpdateTime = 0L;
    private static final long UPDATE_INTERVAL_MS = 500;

    private static List<Tool> activatedTools = new ArrayList<>();
    private static HudTool hudTool;

    private static final Color[] COLORS = {
            Color.RED, Color.ORANGE, Color.YELLOW,
            Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA
    };

    private static void updateTools() {
        long now = System.currentTimeMillis();
        if (now - lastUpdateTime < UPDATE_INTERVAL_MS) return;

        lastUpdateTime = now;
        activatedTools = BlockFighter.toolManager.getActivatedTools(true);
    }

    public static void render(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        if (hudTool == null) {
            hudTool = BlockFighter.toolManager.getToolByClass(HudTool.class);
            if (hudTool == null) return;
        }

        if (!hudTool.isEnabled()) return;
        
        updateTools();

        if (activatedTools.isEmpty()) return;

        int startY = 10;
        int color = getCurrentColor(tickCounter);

        for (Tool tool : activatedTools) {
            context.text(mc.font, tool.getDisplayName(), 10, startY, rainbow ? color : Color.WHITE.getRGB(), true);
            startY += TOOL_SPACING;
        }
    }

    private static int getCurrentColor(DeltaTracker tickCounter) {
        if (!rainbow) return Color.WHITE.getRGB();

        float progress = (System.currentTimeMillis() / 1000f) * colorCycleSpeed;
        int i = (int) progress % COLORS.length;
        int j = (i + 1) % COLORS.length;
        float t = progress % 1f;

        return ColorLerper.getLerpedColor(ColorLerper.Type.SHEEP, tickCounter.getGameTimeDeltaTicks());
    }

    public static void setRainbow(boolean rainbow) {
        ToolHud.rainbow = rainbow;
    }

    public static void setColorCycleSpeed(float colorCycleSpeed) {
        ToolHud.colorCycleSpeed = colorCycleSpeed;
    }

    public static Identifier getHudElement() {
        return hudElement;
    }

    public static Identifier getHudId() {
        return hudId;
    }
}
