package net.normalv.systems.tools.player;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.*;

public class AutoWindChargeTool extends Tool {
    private int minCeilingHeight = 10;

    public AutoWindChargeTool() {
        super("AutoWindCharge", "Auto throws windcharges to get up", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if(!mc.player.getInventory().getItem(WIND_CHARGE_SLOT).is(Items.WIND_CHARGE) ||
                BlockFighter.fightBot.antiWebTool.findIntersectingCobweb() != null ||
                BlockFighter.playerManager.isMacing(BlockFighter.fightBot.getTarget()) ||
                mc.player.distanceTo(BlockFighter.fightBot.getTarget()) > 8 ||
                !mc.player.onGround()) return;

        if(!mc.options.keyJump.isDown()) mc.options.keyJump.setDown(true);

        if(mc.player.onGround() && BlockFighter.playerManager.getDistanceToCeiling(mc.player) > minCeilingHeight) {
            if(mc.player.getInventory().getSelectedSlot() != WIND_CHARGE_SLOT) BlockFighter.playerManager.switchSlot(WIND_CHARGE_SLOT);
            mc.player.setXRot(90);
            BlockFighter.interactionManager.singleUseItem();

            BlockFighter.fightBot.setMacing(true);

            if(mc.player.getInventory().getSelectedSlot() != AXE_SLOT && mc.player.getInventory().getItem(AXE_SLOT).is(ItemTags.AXES)) BlockFighter.playerManager.switchSlot(AXE_SLOT);
            if(mc.options.keyUse.isDown()) mc.options.keyUse.setDown(false);
        }
    }
}