package net.normalv.systems.tools.combat;

import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.util.player.SlotUtils;

public class AutoShieldTool extends Tool {
    public AutoShieldTool() {
        super("AutoShield", "Shields when needed", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if(!mc.player.getInventory().getStack(SlotUtils.OFFHAND).isOf(Items.SHIELD) ||
                mc.player.distanceTo(BlockFighter.fightBot.getTarget()) > 3.1 ||
                mc.player.getAttackCooldownProgress(0.5f) < 1.0) return;

        mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
    }
}
