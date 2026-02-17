package net.normalv.systems.tools.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.*;

public class AuraTool extends Tool {
    private LivingEntity target;

    public AuraTool() {
        super("Aura", "Hits enemies", Category.COMBAT);
    }

    @Override
    public void onTick() {
        target = BlockFighter.targetManager.getCurrentTarget();
        if (target == null || BlockFighter.fightBot.isHealing()) return;

        double maxReach = BlockFighter.fightBot.getMaxReach();

        if (BlockFighter.playerManager.isEatingGapple()) {
            mc.interactionManager.stopUsingItem(mc.player);
            if(mc.player.getInventory().getSelectedSlot() != SWORD_SLOT) BlockFighter.playerManager.switchSlot(SWORD_SLOT);
        }

        if (!BlockFighter.playerManager.isWithinHitboxRange(target, maxReach) || BlockFighter.playerManager.isMacing(target)) return;

        if (target instanceof PlayerEntity targetPlayer && BlockFighter.playerManager.isBlocking(targetPlayer)) {
            handleShieldBreak(target);
            if(!BlockFighter.fightBot.isMacing()) return;
        }

        if(BlockFighter.fightBot.isMacing() && mc.player.getInventory().getStack(MACE_SLOT).isOf(Items.MACE) && mc.player.fallDistance > 3 && mc.player.getVelocity().getY() < 0.0) {
            if(mc.player.getInventory().getSelectedSlot() != MACE_SLOT) BlockFighter.playerManager.switchSlot(MACE_SLOT);

            BlockFighter.playerManager.lookAt(target);
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            return;
        }

        if(mc.player.getInventory().getSelectedSlot() != SWORD_SLOT) BlockFighter.playerManager.switchSlot(SWORD_SLOT);

        if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
            BlockFighter.playerManager.lookAt(target);

            if(shouldCrit() && !canCrit()) return;

            startWTap();
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private void handleShieldBreak(Entity target) {
        if(BlockFighter.playerManager.isBlocking(mc.player)) mc.interactionManager.stopUsingItem(mc.player);

        if(mc.player.getInventory().getSelectedSlot() != AXE_SLOT) BlockFighter.playerManager.switchSlot(AXE_SLOT);
        BlockFighter.playerManager.lookAt(target);
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void startWTap() {
        // Release sprint + forward and press again
        mc.options.sprintKey.setPressed(false);
        mc.options.forwardKey.setPressed(false);
        mc.options.sprintKey.setPressed(true);
        mc.options.forwardKey.setPressed(true);
    }

    private boolean shouldCrit() {
        return !mc.player.isOnGround() && !BlockFighter.playerManager.isBlocking(target);
    }

    private boolean canCrit() {
        return !mc.player.isOnGround() && mc.player.getVelocity().getY() < -0.08;
    }
}