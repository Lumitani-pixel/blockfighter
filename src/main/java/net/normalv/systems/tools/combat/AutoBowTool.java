package net.normalv.systems.tools.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.BOW_SLOT;

public class AutoBowTool extends Tool {

    private LivingEntity target;
    private int drawTicks = 0;

    private static final int MAX_DRAW_TICKS = 20;

    public AutoBowTool() {
        super("AutoBow", "Automatically shoots people", Category.COMBAT);
    }

    @Override
    public void onDisabled() {
        drawTicks = 0;
        mc.interactionManager.stopUsingItem(mc.player);
        mc.options.useKey.setPressed(false);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (!mc.player.getInventory().getStack(BOW_SLOT).isOf(Items.BOW)) return;

        target = BlockFighter.fightBot.getTarget();
        if (target == null) {
            resetBow();
            return;
        }

        // Switch to bow
        if(mc.player.getInventory().getSelectedSlot() != BOW_SLOT) BlockFighter.playerManager.switchSlot(BOW_SLOT);

        // Stop Jumping
        if(mc.options.jumpKey.isPressed()) mc.options.jumpKey.setPressed(false);

        // Aim
        float[] bowRotations = BlockFighter.playerManager.getBowRotationsTo(target);
        mc.player.setYaw(bowRotations[0]);
        mc.player.setPitch(bowRotations[1]);
        mc.player.setHeadYaw(bowRotations[0]);

        // Start drawing
        if (!mc.player.isUsingItem()) {
            mc.options.useKey.setPressed(true);
            drawTicks = 0;
            return;
        }

        drawTicks++;

        if (drawTicks >= MAX_DRAW_TICKS) {
            mc.interactionManager.stopUsingItem(mc.player);
            mc.options.useKey.setPressed(false);
            drawTicks = 0;
        }
    }

    private void resetBow() {
        if (mc.player.isUsingItem()) {
            mc.interactionManager.stopUsingItem(mc.player);
            mc.options.useKey.setPressed(false);
        }
        drawTicks = 0;
    }
}