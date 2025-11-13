package net.normalv.systems.fightbot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.normalv.BlockFighter;
import net.normalv.event.events.impl.AttackBlockEvent;
import net.normalv.event.events.impl.AttackEntityEvent;
import net.normalv.event.system.Subscribe;
import net.normalv.systems.fightbot.pathing.PathingHelper;
import net.normalv.util.interfaces.Util;

import java.util.List;

public class FightBot implements Util {
    private Entity target;
    private double maxReach = 3.0;
    private boolean isEnabled = false;

    private PathingHelper pathingHelper = new PathingHelper();

    public FightBot() {
        EVENT_BUS.register(this);
    }

    public void onTick() {
        if(target==null || !target.isAlive()) {
            target = BlockFighter.targetManager.getCurrentTarget();
            BlockFighter.textManager.sendTextClientSide(Text.literal("Invalid target assigning new").formatted(Formatting.RED));
            return;
        }

        if(mc.player.getInventory().getSelectedStack().isOf(Items.BOW)) {
            float[] rotationsToTarget = BlockFighter.playerManager.getBowRotationsTo(target);
            mc.player.setYaw(rotationsToTarget[0]);
            mc.player.setPitch(rotationsToTarget[1]);
        }
    }

    @Subscribe
    public void onAttackBlock(AttackBlockEvent event) {
        if(!isEnabled) return;
    }

    @Subscribe
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
