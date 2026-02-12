package net.normalv.systems.fightbot;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.normalv.BlockFighter;
import net.normalv.event.events.impl.AttackBlockEvent;
import net.normalv.event.events.impl.AttackEntityEvent;
import net.normalv.systems.fightbot.pathing.PathingHelper;
import net.normalv.util.Util;

public class FightBot implements Util {
    private Entity target;
    private double maxReach = 3.0;
    private boolean isEnabled = false;

    private PathingHelper pathingHelper = new PathingHelper();

    public FightBot() {
        EVENT_BUS.register(this);
    }

    //TODO we might need some actual bot in a fighting bot not just a hold item to action system
    public void onTick() {
        if(target==null || !target.isAlive()) {
            target = BlockFighter.targetManager.getCurrentTarget();
            return;
        }

        pathingHelper.goToEntity(target);

        if(mc.player.distanceTo(target) <= maxReach && mc.player.getAttackCooldownProgress(0.5f) >= 1) {
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
            mc.interactionManager.attackEntity(mc.player, target);
        }
    }

    public void onAttackBlock(AttackBlockEvent event) {
        if(!isEnabled) return;
    }

    public void onAttackEntity(AttackEntityEvent event) {
        if(!isEnabled) return;
    }

    private void enable(){
        BlockFighter.textManager.sendTextClientSide(Text.literal("FightBot enabled"));
        isEnabled = true;
    }

    private void disable() {
        BlockFighter.textManager.sendTextClientSide(Text.literal("FightBot disabled"));
        isEnabled = false;
    }

    public void toggle() {
        if(!isEnabled) enable();
        else disable();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public double getMaxReach() {
        return maxReach;
    }

    public void setMaxReach(double maxReach) {
        this.maxReach = Math.min(maxReach, 5.9);
    }
}
