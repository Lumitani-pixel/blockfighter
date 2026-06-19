package net.normalv.event.events.impl;

import net.minecraft.world.entity.Entity;
import net.normalv.event.events.Event;

public class TargetChangeEvent extends Event {
    public Entity oldTarget;
    public Entity newTarget;

    public TargetChangeEvent(Entity oldTarget, Entity newTarget) {
        this.oldTarget = oldTarget;
        this.newTarget = newTarget;
    }
}
