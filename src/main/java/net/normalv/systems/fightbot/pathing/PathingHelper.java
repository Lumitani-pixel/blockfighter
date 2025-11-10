package net.normalv.systems.fightbot.pathing;

import net.minecraft.util.math.BlockPos;

//TODO: Tidy up and optimize the whole class
public class PathingHelper {
    private double baritoneUseDistance = 20;
    private boolean onlyUseBaritone = true;
    private BlockPos currentGoal;
    private int goalCooldown = 0;

    public boolean shouldUseBaritone(double distance) {
        if(onlyUseBaritone) return true;
        return distance>=baritoneUseDistance;
    }

    public boolean onlyUseBaritone() {
        return onlyUseBaritone;
    }
}
