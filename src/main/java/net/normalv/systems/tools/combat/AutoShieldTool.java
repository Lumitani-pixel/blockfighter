package net.normalv.systems.tools.combat;

import net.minecraft.entity.Entity;
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
        Entity target = BlockFighter.fightBot.getTarget();
        if (target == null) return;

        if (!mc.player.getInventory().getStack(40).isOf(Items.SHIELD)) return;
        if (mc.player.distanceTo(target) > 3.9) {
            if(BlockFighter.playerManager.isBlocking(mc.player)) mc.player.stopUsingItem();
            return;
        }
        if (mc.player.getAttackCooldownProgress(0.5f) < 1.0f) return;
        if (BlockFighter.playerManager.shouldHeal()) return;

        mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
    }
}
