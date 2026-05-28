package net.normalv.systems.fightbot.pathing;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;

public class FollowEntityGoal implements Goal {
    private final Entity entity;
    private final double closeEnoughDistance;

    public FollowEntityGoal(Entity entity, double closeEnoughDistance) {
        this.entity = entity;
        this.closeEnoughDistance = closeEnoughDistance;
    }

    @Override
    public boolean isInGoal(int x, int y, int z) {
        BlockPos p = new BlockPos(x, y, z);
        Vec3i entityPos = new Vec3i(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
        return entity.getBlockPosBelowThatAffectsMyMovement().equals(p) || p.closerThan(entityPos, closeEnoughDistance);
    }

    @Override
    public double heuristic(int x, int y, int z) {
        double xDiff = x - entity.asLivingEntity().getX();
        int yDiff = (int) (y - entity.asLivingEntity().getY());
        double zDiff = z - entity.asLivingEntity().getZ();
        return GoalBlock.calculate(xDiff, yDiff, zDiff);
    }
}
