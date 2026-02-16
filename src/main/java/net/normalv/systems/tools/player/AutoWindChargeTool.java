package net.normalv.systems.tools.player;

import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.MACE_SLOT;
import static net.normalv.systems.fightbot.FightBot.WIND_CHARGE_SLOT;

public class AutoWindChargeTool extends Tool {
    public AutoWindChargeTool() {
        super("AutoWindCharge", "Auto throws windcharges to get up", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if(!mc.player.getInventory().getStack(WIND_CHARGE_SLOT).isOf(Items.WIND_CHARGE)) return;

        if(!mc.options.jumpKey.isPressed()) mc.options.jumpKey.setPressed(true);

        if(mc.player.isOnGround()) {
            if(mc.player.getInventory().getSelectedSlot() != WIND_CHARGE_SLOT) BlockFighter.playerManager.switchSlot(WIND_CHARGE_SLOT);
            mc.player.setPitch(90);

            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);

            if(mc.player.getInventory().getSelectedSlot() != MACE_SLOT) BlockFighter.playerManager.switchSlot(MACE_SLOT);
        }
    }
}
