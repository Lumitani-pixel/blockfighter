package net.normalv.systems.tools.player;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
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
        if((!mc.player.getInventory().getItem(WATER_SLOT).is(Items.WATER_BUCKET) && (!mc.player.getInventory().getItem(WATER_SLOT).is(Items.BUCKET)) ||
                (findIntersectingCobweb() == null && (!mc.level.getBlockState(mc.player.getBlockPosBelowThatAffectsMyMovement()).is(Blocks.WATER))))) return;

        if(mc.player.getInventory().getItem(WATER_SLOT).is(Items.WATER_BUCKET) && findIntersectingCobweb() == null) return;
            // Replace this with a potential sword mining option
        else if(mc.player.getInventory().getItem(WATER_SLOT).is(Items.BUCKET) && findIntersectingCobweb() != null && waterPlacePos == null) return;

        if(mc.player.getInventory().getSelectedSlot() != WATER_SLOT) BlockFighter.playerManager.switchSlot(WATER_SLOT);

        BlockPos blockPos = waterPlacePos == null ? findIntersectingCobweb() : waterPlacePos;

        if(blockPos == null) return;

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePosition(), blockPos.getCenter());
        mc.player.setYRot(rotation[0]);
        mc.player.setXRot(rotation[1]);
        mc.player.setYHeadRot(rotation[0]);

        mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
        mc.player.swing(InteractionHand.MAIN_HAND);

        if(waterPlacePos == null) waterPlacePos = blockPos.above();
            // Make sure we actually took the water
        else if(!mc.level.getBlockState(waterPlacePos).is(Blocks.WATER)) waterPlacePos = null;

        delay = 5;
    }

    public BlockPos findIntersectingCobweb() {
        AABB aabb = mc.player.getBoundingBox();

        int minX = Mth.floor(aabb.minX);
        int minY = Mth.floor(aabb.minY);
        int minZ = Mth.floor(aabb.minZ);
        int maxX = Mth.ceil(aabb.maxX);
        int maxY = Mth.ceil(aabb.maxY);
        int maxZ = Mth.ceil(aabb.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (mc.level.getBlockState(pos).is(Blocks.COBWEB)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}