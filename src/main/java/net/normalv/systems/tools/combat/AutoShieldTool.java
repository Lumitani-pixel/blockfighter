package net.normalv.systems.tools.combat;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import java.util.List;

public class AutoShieldTool extends Tool {
    public AutoShieldTool() {
        super("AutoShield", "Shields when needed", Category.COMBAT);
    }

    @Override
    public void onDisabled() {
        if(BlockFighter.playerManager.isBlocking(mc.player)) {
            mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
        }
    }

    @Override
    public void onTick() {
        LivingEntity target = BlockFighter.fightBot.getTarget();
        List<AbstractArrow> arrowList = BlockFighter.targetManager.getArrows();
        if (target == null) return;

        if (!mc.player.getInventory().getItem(40).is(Items.SHIELD)) return;

        if(BlockFighter.fightBot.shieldIsRequired()) {
            startShielding();
        }
        else if(!arrowList.isEmpty()) {
            for(AbstractArrow arrow : arrowList) {
                Vec3 toPlayer = mc.player.position().subtract(arrow.position()).normalize();
                Vec3 velocity = arrow.getDeltaMovement().normalize();

                if (velocity.dot(toPlayer) < 0.8) {
                    continue;
                }

                if(mc.player.distanceTo(arrow) < 5) {
                    startShielding();
                    mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, arrow.position());
                    break;
                }
            }
        }
        else if (!BlockFighter.playerManager.isWithinHitboxRangeHorizontal(target, BlockFighter.fightBot.getMaxReach()+0.9) ||
                BlockFighter.playerManager.shouldHeal() ||
                (mc.player.getAttackStrengthScale(0.5f) >= 0.99f && BlockFighter.fightBot.auraTool.isEnabled()) ||
                BlockFighter.fightBot.isMacing()) {

            stopShielding();
        }
    }

    private void startShielding() {
        if (!mc.player.isUsingItem()) {
            mc.options.keyUse.setDown(true);
            mc.gameMode.useItem(mc.player, InteractionHand.OFF_HAND);
        }
    }

    private void stopShielding() {
        if(BlockFighter.playerManager.isBlocking(mc.player)) {
            mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
        }
    }
}
