package net.normalv.systems.hud.hudcomponents;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.normalv.BlockFighter;
import net.normalv.systems.fightbot.FightBot;
import net.normalv.util.Util;
import org.apache.logging.log4j.core.pattern.TextRenderer;

import java.awt.*;

public class InfoHud implements Util {

    private static final Identifier HUD_ELEMENT = VanillaHudElements.MISC_OVERLAYS;
    private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath(BlockFighter.MOD_ID, "info_hud");

    // smooth values
    private static float healthAnim = 0;
    private static float cooldownAnim = 0;

    public static void render(GuiGraphicsExtractor ctx, DeltaTracker counter) {
        FightBot bot = BlockFighter.fightBot;
        if (bot == null || !bot.isEnabled() || bot.infoHudTool == null || !bot.infoHudTool.isEnabled() || bot.getTarget() == null) return;

        int sw = ctx.guiWidth();
        int sh = ctx.guiHeight();

        Color accent = stateColor(bot);

        drawTopStatus(ctx, sw / 2 - 70, 52, bot, accent);
        drawVitals(ctx, 12, sh / 2 - 40, accent);
        drawCombat(ctx, sw - 140, sh / 2 - 40, bot, accent);
        drawMotion(ctx, sw / 2 - 90, sh - 62, bot, accent);
    }

    private static void drawTopStatus(GuiGraphicsExtractor ctx, int x, int y, FightBot bot, Color accent) {
        panel(ctx, x, y, 140, 26, accent);

        ctx.text(
                mc.font,
                "FIGHTBOT / " + bot.state,
                x + 6,
                y + 6,
                Color.WHITE.hashCode()
        );
    }

    private static void drawVitals(GuiGraphicsExtractor ctx, int x, int y, Color accent) {
        panel(ctx, x, y, 110, 36, accent);

        float hp = mc.player.getHealth() / mc.player.getMaxHealth();
        healthAnim += (hp - healthAnim) * 0.15f;

        drawBar(ctx, x + 6, y + 18, 90, healthAnim, new Color(220, 70, 70));
        ctx.text(
                mc.font,
                "HP",
                x + 6,
                y + 6,
                Color.LIGHT_GRAY.hashCode()
        );
    }

    private static void drawCombat(GuiGraphicsExtractor ctx, int x, int y, FightBot bot, Color accent) {
        panel(ctx, x, y, 120, 70, accent);

        float cd = mc.player.getAttackStrengthScale(0);
        cooldownAnim += (cd - cooldownAnim) * 0.25f;
        boolean canCrit = !mc.player.onGround() && mc.player.getDeltaMovement().y < -0.08;

        ctx.text(mc.font,
                "CD",
                x + 6,
                y + 6,
                Color.LIGHT_GRAY.hashCode()
        );

        drawBar(ctx, x + 6, y + 16, 90, cooldownAnim, new Color(80, 160, 255));

        ctx.text(
                mc.font,
                "CRIT " + (canCrit ? "READY" : "NO"),
                x + 6,
                y + 28,
                canCrit ? Color.GREEN.hashCode() : Color.GRAY.hashCode()
        );

        ctx.text(
                mc.font,
                "DISTANCE " + format(mc.player.distanceTo(bot.getTarget()))  + " / " + bot.getMaxReach(),
                x + 6,
                y + 50,
                mc.player.distanceTo(bot.getTarget()) > bot.getMaxReach() ? Color.LIGHT_GRAY.hashCode() : Color.GREEN.hashCode()
        );
    }

    private static void drawMotion(GuiGraphicsExtractor ctx, int x, int y, FightBot bot, Color accent) {
        panel(ctx, x, y, 180, 26, accent);

        double speed = mc.player.getDeltaMovement().horizontalDistance();
        boolean falling = !mc.player.onGround() && mc.player.getDeltaMovement().y < 0;

        ctx.text(
                mc.font,
                "SPD " + format(speed) + " | FALL " + (falling ? "YES" : "NO"),
                x + 6,
                y + 6,
                Color.LIGHT_GRAY.hashCode()
        );
    }

    private static void panel(GuiGraphicsExtractor ctx, int x, int y, int w, int h, Color accent) {
        ctx.fill(x, y, x + w, y + h, new Color(10, 12, 16, 160).hashCode());
        ctx.fill(x, y, x + w, y + 1, accent.hashCode());
        ctx.fill(x, y, x + 1, y + h, accent.hashCode());
    }

    private static void drawBar(GuiGraphicsExtractor ctx, int x, int y, int w, float pct, Color color) {
        ctx.fill(x, y, x + w, y + 4, new Color(30, 30, 35, 140).hashCode());
        ctx.fill(x, y, x + (int)(w * Mth.clamp(pct, 0, 1)), y + 4, color.hashCode());
    }

    private static Color stateColor(FightBot bot) {
        return switch (bot.state) {
            case ATTACKING -> new Color(230, 70, 70);
            case CHASING -> new Color(80, 160, 255);
            case HEALING -> new Color(70, 220, 140);
            default -> new Color(150, 150, 150);
        };
    }

    private static String format(double v) {
        return String.format("%.2f", v);
    }

    public static Identifier getHudElement() {
        return HUD_ELEMENT;
    }

    public static Identifier getHudId() {
        return HUD_ID;
    }
}