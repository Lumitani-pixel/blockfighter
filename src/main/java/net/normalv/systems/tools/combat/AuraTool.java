package net.normalv.systems.tools.combat;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.*;

public class AuraTool extends Tool {
    private double minSpearVelocity = 20.0;

    private LivingEntity target;

    public AuraTool() {
        super("Aura", "Hits enemies", Category.COMBAT);
    }

    @Override
    public void onTick() {
        target = BlockFighter.targetManager.getCurrentTarget();
        if (target == null || BlockFighter.fightBot.isHealing()) return;

        double maxReach = BlockFighter.fightBot.getMaxReach();
        double spearReach = BlockFighter.fightBot.getSpearReach();

        if (BlockFighter.playerManager.isEatingGapple()) {
            mc.options.useKey.setPressed(false);
            mc.interactionManager.stopUsingItem(mc.player);
        }

        if(!BlockFighter.playerManager.canSeeEntity(target)) return;

        // We subtract a little buffer to not set off ac flags (Still getting some reach flags HOW??)
        if (BlockFighter.playerManager.isWithinHitboxRange(target, spearReach-0.1) &&
                !BlockFighter.playerManager.isWithinHitboxRange(target, maxReach-0.1) &&
                mc.player.getInventory().getStack(SPEAR_SLOT).isIn(ItemTags.SPEARS) ||
                (mc.player.getVelocity().subtract(target.getVelocity()).length() * 20.0) > minSpearVelocity) {

            if(mc.player.getInventory().getSelectedSlot() != SPEAR_SLOT) BlockFighter.playerManager.switchSlot(SPEAR_SLOT);

            Vec3d hitVec = BlockFighter.playerManager.getHitVec(target);
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, hitVec);

            if((mc.player.getVelocity().subtract(target.getVelocity()).length() * 20.0) > minSpearVelocity) {
                if(!mc.options.useKey.isPressed()) mc.options.useKey.setPressed(true);
                if(!mc.player.isUsingItem()) mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
            else if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }

        // We subtract a little buffer to not set off ac flags (Still getting some reach flags HOW??)
        if (!BlockFighter.playerManager.isWithinHitboxRange(target, maxReach-0.1) || BlockFighter.playerManager.isMacing(target)) return;

        if (target instanceof PlayerEntity targetPlayer && BlockFighter.playerManager.isBlocking(targetPlayer)) {
            handleShieldBreak(target);
            if(!BlockFighter.fightBot.isMacing()) return;
        }

        if(BlockFighter.fightBot.isMacing() && mc.player.getInventory().getStack(MACE_SLOT).isOf(Items.MACE) && mc.player.fallDistance > 3 && mc.player.getVelocity().getY() < 0.0) {
            if(mc.player.getInventory().getSelectedSlot() != MACE_SLOT) BlockFighter.playerManager.switchSlot(MACE_SLOT);

            Vec3d hitVec = BlockFighter.playerManager.getHitVec(target);
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, hitVec);

            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            return;
        }

        if(mc.player.getInventory().getSelectedSlot() != SWORD_SLOT && !BlockFighter.fightBot.isMacing()) BlockFighter.playerManager.switchSlot(SWORD_SLOT);

        if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
            Vec3d hitVec = BlockFighter.playerManager.getHitVec(target);
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, hitVec);


            if(shouldCrit() && !canCrit()) return;

            startWTap();
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private void handleShieldBreak(Entity target) {
        if(BlockFighter.playerManager.isBlocking(mc.player)) {
            mc.options.useKey.setPressed(false);
            mc.interactionManager.stopUsingItem(mc.player);
        }

        if(mc.player.getInventory().getSelectedSlot() != AXE_SLOT) BlockFighter.playerManager.switchSlot(AXE_SLOT);

        Vec3d hitVec = BlockFighter.playerManager.getHitVec(target);
        mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, hitVec);

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
        return !mc.player.isOnGround() && !BlockFighter.playerManager.isBlocking(target) && BlockFighter.fightBot.antiWebTool.findIntersectingCobweb() == null;
    }

    private boolean canCrit() {
        return !mc.player.isOnGround() && mc.player.getVelocity().getY() < -0.08;
    }
}