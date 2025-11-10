package net.normalv.systems.fightbot.pathing;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

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
        return entity.getBlockPos().equals(p) || p.isWithinDistance(entity.getEntityPos(), closeEnoughDistance);
    }

    @Override
    public double heuristic(int x, int y, int z) {
        double xDiff = x - entity.getEntity().getX();
        int yDiff = y - entity.getBlockPos().getY();
        double zDiff = z - entity.getEntity().getZ();
        return GoalBlock.calculate(xDiff, yDiff, zDiff);
    }
}
