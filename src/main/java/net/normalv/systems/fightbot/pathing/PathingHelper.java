package net.normalv.systems.fightbot.pathing;

import baritone.api.BaritoneAPI;
import net.minecraft.entity.Entity;
import net.normalv.BlockFighter;
import net.normalv.util.Util;

public class PathingHelper implements Util {
    private double baritoneUseDistance = 20;
    private boolean onlyUseBaritone = false;
    private boolean pathing = false;
    private boolean allowJumping = true;

    public void goToEntity(Entity target) {
        pathing = true;

        if(shouldUseBaritone(target)) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new FollowEntityGoal(target, BlockFighter.fightBot.getMaxReach()-0.1));
            return;
        }
        else if(BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) stopPathing();

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePos(), target.getEyePos());
        manualPathing(rotation, target);
    }

    public void manualPathing(float[] rotation, Entity target) {
        mc.player.setYaw(rotation[0]);
        mc.player.setPitch(rotation[1]);
        mc.player.setHeadYaw(rotation[0]);

        if(mc.player.distanceTo(target) > 3.1) {
            mc.options.sprintKey.setPressed(true);
            mc.options.forwardKey.setPressed(true);
            mc.options.jumpKey.setPressed(allowJumping);
            mc.options.backKey.setPressed(false);
        } else if(mc.player.distanceTo(target) < 2.9) {
            mc.options.sprintKey.setPressed(false);
            mc.options.forwardKey.setPressed(false);
            mc.options.jumpKey.setPressed(false);
            mc.options.backKey.setPressed(true);
        }
    }

    public boolean shouldUseBaritone(Entity target) {
        if(onlyUseBaritone) return true;
        return mc.player.distanceTo(target)>=baritoneUseDistance;
    }

    public void stopPathing() {
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
        pathing = false;
    }

    public boolean isPathing() {
        return pathing;
    }

    public boolean onlyUseBaritone() {
        return onlyUseBaritone;
    }
}
