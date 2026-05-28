package net.normalv.systems.tools.combat;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
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
        mc.options.keyUse.setDown(false);
        mc.gameMode.releaseUsingItem(mc.player);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (!mc.player.getInventory().getItem(BOW_SLOT).is(Items.BOW)) return;

        target = BlockFighter.fightBot.getTarget();
        if (target == null) {
            resetBow();
            return;
        }

        // Switch to bow
        if(mc.player.getInventory().getSelectedSlot() != BOW_SLOT) BlockFighter.playerManager.switchSlot(BOW_SLOT);

        // Stop Jumping
        if(mc.options.keyJump.isDown()) mc.options.keyJump.setDown(false);

        // Aim
        float[] bowRotations = BlockFighter.playerManager.getBowRotationsTo(target);
        mc.player.setYRot(bowRotations[0]);
        mc.player.setXRot(bowRotations[1]);
        mc.player.setYHeadRot(bowRotations[0]);

        // Start drawing
        if (!mc.player.isUsingItem()) {
            mc.options.keyUse.setDown(true);
            mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            drawTicks = 0;
            return;
        }

        drawTicks++;

        if (drawTicks >= MAX_DRAW_TICKS) {
            mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
            drawTicks = 0;
        }
    }

    private void resetBow() {
        if (mc.player.isUsingItem()) {
            mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
        }
        drawTicks = 0;
    }
}