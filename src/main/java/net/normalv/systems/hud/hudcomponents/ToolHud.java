package net.normalv.systems.hud.hudcomponents;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.client.HudTool;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolHud extends HudComponent{
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

    public ToolHud() {
        super(VanillaHudElements.MISC_OVERLAYS, Identifier.of(BlockFighter.MOD_ID, "tool_hud"));
    }

    private static void updateTools() {
        long now = System.currentTimeMillis();
        if (now - lastUpdateTime < UPDATE_INTERVAL_MS) return;

        lastUpdateTime = now;
        activatedTools = BlockFighter.toolManager.getActivatedTools(true);
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (hudTool == null) {
            hudTool = BlockFighter.toolManager.getToolByClass(HudTool.class);
            if (hudTool == null) return;
        }

        if (!hudTool.isEnabled()) return;
        
        updateTools();

        if (activatedTools.isEmpty()) return;

        int startY = 10;
        int color = getCurrentColor();

        for (Tool tool : activatedTools) {
            context.drawText(mc.textRenderer, tool.getDisplayName(), 10, startY, rainbow ? color : Color.WHITE.getRGB(), true);
            startY += TOOL_SPACING;
        }
    }

    private static int getCurrentColor() {
        if (!rainbow) return Color.WHITE.getRGB();

        float progress = (System.currentTimeMillis() / 1000f) * colorCycleSpeed;
        int i = (int) progress % COLORS.length;
        int j = (i + 1) % COLORS.length;
        float t = progress % 1f;

        return ColorHelper.lerp(t, COLORS[i].getRGB(), COLORS[j].getRGB());
    }

    public static void setRainbow(boolean rainbow) {
        ToolHud.rainbow = rainbow;
    }

    public static void setColorCycleSpeed(float colorCycleSpeed) {
        ToolHud.colorCycleSpeed = colorCycleSpeed;
    }
}
