package net.normalv.systems.tools.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

public class AutoShieldTool extends Tool {
    public AutoShieldTool() {
        super("AutoShield", "Shields when needed", Category.COMBAT);
    }

    @Override
    public void onTick() {
        LivingEntity target = BlockFighter.fightBot.getTarget();
        if (target == null) return;

        if (!mc.player.getInventory().getStack(40).isOf(Items.SHIELD)) return;
        if (!BlockFighter.playerManager.isWithinHitboxRange(target, BlockFighter.fightBot.getMaxReach()+0.9) ||
                BlockFighter.playerManager.shouldHeal() ||
                (mc.player.getAttackCooldownProgress(0.5f) >= 0.99f && !BlockFighter.playerManager.isMacing(target)) ||
                BlockFighter.fightBot.isMacing()) {

            if(BlockFighter.playerManager.isBlocking(mc.player)) mc.interactionManager.stopUsingItem(mc.player);
            return;
        }

        if (!mc.player.isUsingItem()) {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
        }
    }
}
