package net.normalv.systems.tools.combat;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.*;

public class AuraTool extends Tool {
    private double minSpearVelocity = 20.0;

    private boolean useShieldBreakWithMace = true;

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

        if (BlockFighter.playerManager.isEatingFood()) {
            mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
        }

        if(!BlockFighter.playerManager.canHit(target)) return;

        // We subtract a little buffer to not set off ac flags (Still getting some reach flags HOW??)
        if (BlockFighter.playerManager.isWithinHitboxRange(target, spearReach) &&
                !BlockFighter.playerManager.isWithinHitboxRange(target, maxReach) &&
                mc.player.getInventory().getItem(SPEAR_SLOT).is(ItemTags.SPEARS) ||
                (mc.player.getDeltaMovement().subtract(target.getDeltaMovement()).length() * 20.0) > minSpearVelocity) {

            if(mc.player.getInventory().getSelectedSlot() != SPEAR_SLOT) BlockFighter.playerManager.switchSlot(SPEAR_SLOT);

            Vec3 hitVec = BlockFighter.playerManager.getHitVec(target);
            mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, hitVec);

            if((mc.player.getDeltaMovement().subtract(target.getDeltaMovement()).length() * 20.0) > minSpearVelocity) {
                if(!mc.options.keyUse.isDown()) mc.options.keyUse.setDown(true);
                if(!mc.player.isUsingItem()) mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            }
            else if (mc.player.getAttackStrengthScale(0.5f) >= 1.0f) {
                mc.gameMode.attack(mc.player, target);
                mc.player.swing(InteractionHand.MAIN_HAND);
            }
        }

        if (!BlockFighter.playerManager.isWithinHitboxRange(target, maxReach) || BlockFighter.playerManager.isMacing(target)) return;

        if (target instanceof Player targetPlayer && BlockFighter.playerManager.isBlocking(targetPlayer) && (BlockFighter.fightBot.isMacing() && !useShieldBreakWithMace)) {
            handleShieldBreak(target);
            if(!BlockFighter.fightBot.isMacing()) return;
        }

        if(BlockFighter.fightBot.isMacing() && mc.player.getInventory().getItem(MACE_SLOT).is(Items.MACE) && mc.player.fallDistance > 3 && mc.player.getDeltaMovement().y() < 0.0) {
            if(mc.player.getInventory().getSelectedSlot() != MACE_SLOT) BlockFighter.playerManager.switchSlot(MACE_SLOT);

            Vec3 hitVec = BlockFighter.playerManager.getHitVec(target);
            mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, hitVec);

            // Shield break tech
            if(useShieldBreakWithMace && BlockFighter.playerManager.isBlocking(target)) {
                for(int i = 0; i<10; i++) {
                    mc.gameMode.attack(mc.player, target);
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }
            }

            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND);
            return;
        }

        if(mc.player.getInventory().getSelectedSlot() != SWORD_SLOT && !BlockFighter.fightBot.isMacing()) BlockFighter.playerManager.switchSlot(SWORD_SLOT);

        if (mc.player.getAttackStrengthScale(0.5f) >= 1.0f) {
            Vec3 hitVec = BlockFighter.playerManager.getHitVec(target);
            mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, hitVec);


            if(shouldCrit() && !canCrit()) return;

            startWTap();
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }

    private void handleShieldBreak(Entity target) {
        if(BlockFighter.playerManager.isBlocking(mc.player)) {
            mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
        }

        if(mc.player.getInventory().getSelectedSlot() != AXE_SLOT) BlockFighter.playerManager.switchSlot(AXE_SLOT);

        Vec3 hitVec = BlockFighter.playerManager.getHitVec(target);
        mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, hitVec);

        mc.gameMode.attack(mc.player, target);
        mc.gameMode.attack(mc.player, target);
        mc.gameMode.attack(mc.player, target);
        mc.player.swing(InteractionHand.MAIN_HAND);
    }

    private void startWTap() {
        // Release sprint + forward and press again
        mc.options.keySprint.setDown(false);
        mc.options.keyUp.setDown(false);
        mc.options.keySprint.setDown(true);
        mc.options.keyUp.setDown(true);
    }

    private boolean shouldCrit() {
        return !mc.player.onGround() && !BlockFighter.playerManager.isBlocking(target) && BlockFighter.fightBot.antiWebTool.findIntersectingCobweb() == null;
    }

    private boolean canCrit() {
        return !mc.player.onGround() && mc.player.getDeltaMovement().y() < -0.08;
    }
}