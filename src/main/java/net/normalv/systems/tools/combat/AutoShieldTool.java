package net.normalv.systems.tools.combat;

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
        if(BlockFighter.fightBot.getTarget() == null) return;
        else if(!mc.player.getInventory().getStack(40).isOf(Items.SHIELD) ||
                mc.player.distanceTo(BlockFighter.fightBot.getTarget()) > 3.9 ||
                mc.player.getAttackCooldownProgress(0.5f) < 1.0 ||
                BlockFighter.playerManager.shouldHeal()) return;

        mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
    }
}
