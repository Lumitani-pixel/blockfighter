package net.normalv.systems.tools.player;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.WATER_SLOT;

public class AntiWebTool extends Tool {
    public BlockPos waterPlacePos = null;
    private int delay = 5;

    public AntiWebTool() {
        super("AntiWeb", "Makes sure bot isn't webbed", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if(--delay > 0) return;
        if((!mc.player.getInventory().getStack(WATER_SLOT).isOf(Items.WATER_BUCKET) && (!mc.player.getInventory().getStack(WATER_SLOT).isOf(Items.BUCKET)) ||
                (findIntersectingCobweb() == null && (!mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.WATER))))) return;

        if(mc.player.getInventory().getStack(WATER_SLOT).isOf(Items.WATER_BUCKET) && findIntersectingCobweb() == null) return;
            // Replace this with a potential sword mining option
        else if(mc.player.getInventory().getStack(WATER_SLOT).isOf(Items.BUCKET) && findIntersectingCobweb() != null && waterPlacePos == null) return;

        if(mc.player.getInventory().getSelectedSlot() != WATER_SLOT) BlockFighter.playerManager.switchSlot(WATER_SLOT);

        BlockPos blockPos = waterPlacePos == null ? findIntersectingCobweb() : waterPlacePos;

        if(blockPos == null) return;

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePos(), blockPos.toCenterPos());
        mc.player.setYaw(rotation[0]);
        mc.player.setPitch(rotation[1]);
        mc.player.setHeadYaw(rotation[0]);

        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.swingHand(Hand.MAIN_HAND);

        if(waterPlacePos == null) waterPlacePos = blockPos.up();
            // Make sure we actually took the water
        else if(!mc.world.getBlockState(waterPlacePos).isOf(Blocks.WATER)) waterPlacePos = null;

        delay = 5;
    }

    public BlockPos findIntersectingCobweb() {
        Box box = mc.player.getBoundingBox();

        int minX = MathHelper.floor(box.minX);
        int minY = MathHelper.floor(box.minY);
        int minZ = MathHelper.floor(box.minZ);
        int maxX = MathHelper.ceil(box.maxX);
        int maxY = MathHelper.ceil(box.maxY);
        int maxZ = MathHelper.ceil(box.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (mc.world.getBlockState(pos).isOf(Blocks.COBWEB)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}