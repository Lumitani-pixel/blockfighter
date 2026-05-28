package net.normalv.pathing.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.normalv.util.Util;

public abstract class Goal implements Util {
    protected BlockPos blockPos;
    protected Vec3i vec3i;

    public Goal(BlockPos blockPos) {
        this.blockPos = blockPos;
        vec3i = new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public abstract void update();

    public boolean isInGoal() {
        return mc.player.getBlockPosBelowThatAffectsMyMovement().closerThan(vec3i, 1);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Vec3i getVec3d() {
        return vec3i;
    }

    public double getAlongAxis(Direction.Axis axis) {
        return vec3i.get(axis);
    }
}
