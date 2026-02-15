package net.normalv.systems.tools.player;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.WATER_SLOT;

public class AntiWebTool extends Tool {
    private BlockPos waterPlacePos = null;
    private int delay = 5;

    public AntiWebTool() {
        super("AntiWeb", "Makes sure bot isn't webbed", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if(--delay > 0) return;
        if((!mc.player.getInventory().getStack(WATER_SLOT).isOf(Items.WATER_BUCKET) && (!mc.player.getInventory().getStack(WATER_SLOT).isOf(Items.BUCKET)) ||
                (!mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.COBWEB) && (!mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.WATER))))) return;

        if(mc.player.getInventory().getStack(WATER_SLOT).isOf(Items.WATER_BUCKET) && !mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.COBWEB)) return;

        if(mc.player.getInventory().getSelectedSlot() != WATER_SLOT) BlockFighter.playerManager.switchSlot(WATER_SLOT);

        BlockPos blockPos = waterPlacePos == null ? mc.player.getBlockPos() : waterPlacePos;

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePos(), blockPos.toCenterPos());
        mc.player.setYaw(rotation[0]);
        mc.player.setPitch(rotation[1]);
        mc.player.setHeadYaw(rotation[0]);

        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.swingHand(Hand.MAIN_HAND);

        if(waterPlacePos == null) waterPlacePos = blockPos.up();
        else waterPlacePos = null;

        delay = 5;
    }
}
