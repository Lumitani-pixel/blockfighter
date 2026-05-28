package net.normalv.systems.tools.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.WATER_SLOT;

public class AutoClutchTool extends Tool {
    private int minClutchDistance = 4;
    public boolean placedWater = false;

    public AutoClutchTool() {
        super("AutoClutch", "Tries to clutch with water", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if(placedWater && mc.player.onGround()) {
            placeWater();
            placedWater = false;
            return;
        }
        if(mc.player.onGround() ||
                mc.player.fallDistance <= minClutchDistance ||
                !mc.player.getInventory().getItem(WATER_SLOT).is(Items.WATER_BUCKET)) return;


        if(BlockFighter.playerManager.getDistanceToGround(mc.player) <= BlockFighter.fightBot.getMaxReach()) {
            placeWater();
            placedWater = true;
        }
    }

    private void placeWater() {
        if(mc.player.getInventory().getSelectedSlot() != WATER_SLOT) BlockFighter.playerManager.switchSlot(WATER_SLOT);

        mc.player.setXRot(90);
        mc.options.keyUse.setDown(true);
        mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND.MAIN_HAND);
        mc.options.keyUse.setDown(false);
    }
}
