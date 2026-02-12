package net.normalv.pathing.goal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.normalv.util.Util;

public abstract class Goal implements Util {
    protected BlockPos blockPos;
    protected Vec3d vec3d;

    public Goal(BlockPos blockPos) {
        this.blockPos = blockPos;
        vec3d = blockPos.toCenterPos();
    }

    public abstract void update();

    public boolean isInGoal() {
        return mc.player.getBlockPos().isWithinDistance(vec3d, 1);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Vec3d getVec3d() {
        return vec3d;
    }

    public double getAlongAxis(Direction.Axis axis) {
        return vec3d.getComponentAlongAxis(axis);
    }
}
