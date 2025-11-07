package net.normalv.systems.managers;

import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.normalv.BlockFighter;

public class WorldManager extends Manager{
    private boolean isBreakingBlock = false;
    private BlockPos breakPos;

    public boolean breakBlock(BlockPos pos) {
        if (mc.player == null || mc.interactionManager == null) return false;
        breakPos = pos;
        if(!mc.world.getBlockState(breakPos).isAir() && !isBreakingBlock && !isBlockOutOfReach(pos)) {
            mc.interactionManager.attackBlock(breakPos, mc.player.getFacing());
            isBreakingBlock = true;
            return true;
        }
        return false;
    }

    public boolean placeBlock(BlockPos base, Direction face) {
        if (mc.player == null || mc.interactionManager == null) return false;

        Vec3d hitVec = Vec3d.ofCenter(base).add(Vec3d.of(face.getVector()).multiply(0.5));
        BlockHitResult hit = new BlockHitResult(hitVec, face, base, false);

        ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);

        return result.isAccepted();
    }

    public boolean isBlockOutOfReach(BlockPos pos) {
        return mc.player.squaredDistanceTo(Vec3d.ofCenter(pos).add(Vec3d.of(mc.player.getFacing().getVector())).multiply(0.5))>BlockFighter.fightBot.getMaxReach();
    }

    public Entity getNearestEntityByClass(Class<Entity> clazz) {
        for(Entity entity : mc.world.getEntities()) {
            if(!clazz.isInstance(entity)) continue;
            return entity;
        }
        return null;
    }

    public void onTick() {
        if(isBreakingBlock) {
            assert mc.interactionManager != null;
            mc.interactionManager.updateBlockBreakingProgress(breakPos, mc.player.getFacing());
        }
    }
}