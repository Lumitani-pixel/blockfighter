package net.normalv.systems.fightbot.pathing;

import baritone.api.BaritoneAPI;
import net.minecraft.world.entity.Entity;
import net.normalv.BlockFighter;
import net.normalv.event.events.impl.TargetChangeEvent;
import net.normalv.event.system.Subscribe;
import net.normalv.util.Util;

public class PathingHelper implements Util {
    private double baritoneUseDistance = 20;
    private boolean onlyUseBaritone = false;
    private boolean useBaritone;
    private boolean allowJumping = true;

    public PathingHelper() {
        EVENT_BUS.register(this);
    }

    public void goToEntity(Entity target) {
        updateUseBaritone(target);

        if(useBaritone && !BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            releaseAllKeys();
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new FollowEntityGoal(target, BlockFighter.fightBot.getMaxReach()-0.1));
            return;
        }

        if(!useBaritone) {
            if(BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();

            float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePosition(), target.getEyePosition());
            manualPathing(rotation, target);
        }
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

    public void updateUseBaritone(Entity target) {
        useBaritone = mc.player.distanceTo(target)>=baritoneUseDistance || !mc.player.hasLineOfSight(target) || onlyUseBaritone;
    }

    public void stopPathing() {
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
    }

    @Subscribe
    public void onTargetChange(TargetChangeEvent event) {
        stopPathing();
        releaseAllKeys();
    }

    public void releaseAllKeys() {
        mc.options.keyDown.setDown(false);
        mc.options.keyUp.setDown(false);
        mc.options.keyJump.setDown(false);
        mc.options.keyLeft.setDown(false);
        mc.options.keyRight.setDown(false);
    }

    public boolean onlyUseBaritone() {
        return onlyUseBaritone;
    }
}
