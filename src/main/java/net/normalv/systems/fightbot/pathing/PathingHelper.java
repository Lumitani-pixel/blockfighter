package net.normalv.systems.fightbot.pathing;

import baritone.api.BaritoneAPI;
import net.minecraft.entity.Entity;
import net.normalv.BlockFighter;

public class PathingHelper {
    private double baritoneUseDistance = 20;
    private boolean onlyUseBaritone = true;
    private int goalCooldown = 0;

    public void goToEntity(Entity target) {
        goalCooldown++;
        if(goalCooldown!=5) return;
        goalCooldown=0;
        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new FollowEntityGoal(target, BlockFighter.fightBot.getMaxReach()));
    }

    public boolean shouldUseBaritone(double distance) {
        if(onlyUseBaritone) return true;
        return distance>=baritoneUseDistance;
    }

    public boolean onlyUseBaritone() {
        return onlyUseBaritone;
    }
}
