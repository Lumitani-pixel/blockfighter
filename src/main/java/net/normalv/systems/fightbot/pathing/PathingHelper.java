package net.normalv.systems.fightbot.pathing;

import baritone.api.BaritoneAPI;
import net.minecraft.world.entity.Entity;
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

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePosition(), target.getEyePosition());
        manualPathing(rotation, target);
    }

    public void manualPathing(float[] rotation, Entity target) {
        mc.player.setYRot(rotation[0]);
        mc.player.setXRot(rotation[1]);
        mc.player.setYHeadRot(rotation[0]);

        if(mc.player.distanceTo(target) > 3.1) {
            mc.options.keySprint.setDown(true);
            mc.options.keyUp.setDown(true);
            mc.options.keyJump.setDown(allowJumping);
            mc.options.keyDown.setDown(false);

        } else if(mc.player.distanceTo(target) < 2.9) {
            mc.options.keySprint.setDown(false);
            mc.options.keyUp.setDown(false);
            mc.options.keyJump.setDown(false);
            mc.options.keyDown.setDown(true);
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
