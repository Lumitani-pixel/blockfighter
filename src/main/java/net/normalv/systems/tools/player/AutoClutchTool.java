package net.normalv.systems.tools.player;

import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.WATER_SLOT;

public class AutoClutchTool extends Tool {
    private int minClutchDistance = 4;
    private boolean placedWater = false;

    public AutoClutchTool() {
        super("AutoClutch", "Tries to clutch with water", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if(placedWater && mc.player.isOnGround()) {
            placeWater();
            placedWater = false;
            return;
        }
        if(mc.player.isOnGround() ||
                mc.player.fallDistance <= minClutchDistance ||
                !mc.player.getInventory().getStack(WATER_SLOT).isOf(Items.WATER_BUCKET)) return;


        if(BlockFighter.playerManager.getDistanceToGround(mc.player) <= BlockFighter.fightBot.getMaxReach()) {
            placeWater();
            placedWater = true;
        }
    }

    private void placeWater() {
        if(mc.player.getInventory().getSelectedSlot() != WATER_SLOT) BlockFighter.playerManager.switchSlot(WATER_SLOT);

        mc.player.setPitch(90);
        mc.options.useKey.setPressed(true);
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.options.useKey.setPressed(false);
    }
}
