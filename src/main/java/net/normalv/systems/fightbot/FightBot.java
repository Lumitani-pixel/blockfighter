package net.normalv.systems.fightbot;

import net.minecraft.text.Text;
import net.normalv.BlockFighter;
import net.normalv.event.events.impl.AttackBlockEvent;
import net.normalv.event.events.impl.AttackEntityEvent;
import net.normalv.event.system.Subscribe;
import net.normalv.util.interfaces.Util;

public class FightBot implements Util {
    private double maxReach = 3.0;
    private boolean isEnabled = false;

    public FightBot() {
        EVENT_BUS.register(this);
    }

    public void onTick() {
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
