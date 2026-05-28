package net.normalv.systems.tools.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.*;

public class AutoWindChargeTool extends Tool {
    public AutoWindChargeTool() {
        super("AutoWindCharge", "Auto throws windcharges to get up", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if(!mc.player.getInventory().getItem(WIND_CHARGE_SLOT).is(Items.WIND_CHARGE) ||
                BlockFighter.fightBot.antiWebTool.findIntersectingCobweb() != null ||
                BlockFighter.playerManager.isMacing(BlockFighter.fightBot.getTarget()) ||
                mc.player.distanceTo(BlockFighter.fightBot.getTarget()) > 8) return;

        if(!mc.options.keyJump.isDown()) mc.options.keyJump.setDown(true);

        if(mc.player.onGround()) {
            if(mc.player.getInventory().getSelectedSlot() != WIND_CHARGE_SLOT) BlockFighter.playerManager.switchSlot(WIND_CHARGE_SLOT);
            mc.player.setXRot(90);

            if(!mc.options.keyUse.isDown()) mc.options.keyUse.setDown(true);
            mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            mc.player.swing(InteractionHand.MAIN_HAND);

            BlockFighter.fightBot.setMacing(true);

            if(mc.player.getInventory().getSelectedSlot() != AXE_SLOT) BlockFighter.playerManager.switchSlot(AXE_SLOT);
            if(mc.options.keyUse.isDown()) mc.options.keyUse.setDown(false);
        }
    }
}