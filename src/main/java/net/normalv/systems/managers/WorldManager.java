package net.normalv.systems.managers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.normalv.BlockFighter;

public class WorldManager extends Manager{
    private boolean isBreakingBlock = false;
    private BlockPos breakPos;

    public boolean breakBlock(BlockPos pos) {
        if (mc.player == null || mc.gameMode == null) return false;
        breakPos = pos;
        if(!mc.level.getBlockState(breakPos).isAir() && !isBreakingBlock && !isBlockOutOfReach(pos)) {
            mc.gameMode.startDestroyBlock(breakPos, mc.player.getDirection());
            isBreakingBlock = true;
            return true;
        }
        return false;
    }

    public boolean placeBlock(BlockPos base, Direction face) {
        if (mc.player == null || mc.gameMode == null) return false;

        Vec3 hitVec = Vec3.atCenterOf(base).add(Vec3.atLowerCornerOf(face.getUnitVec3i()).scale(0.5));
        BlockHitResult hit = new BlockHitResult(hitVec, face, base, false);

        InteractionResult result = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);

        return result.consumesAction();
    }

    public boolean isBlockOutOfReach(BlockPos pos) {
        return mc.player.distanceToSqr(Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(mc.player.getDirection().getUnitVec3i()).scale(0.5)))>BlockFighter.fightBot.getMaxReach();
    }

    public Entity getNearestEntityByClass(Class<Entity> clazz) {
        for(Entity entity : mc.level.entitiesForRendering()) {
            if(!clazz.isInstance(entity)) continue;
            return entity;
        }
        return null;
    }

    public void onTick() {
        if(isBreakingBlock) {
            assert mc.gameMode != null;
            mc.gameMode.continueDestroyBlock(breakPos, mc.player.getDirection());
        }
    }
}