package net.normalv.event.events.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.normalv.event.events.Event;

public class AttackEntityEvent extends Event {
    private PlayerEntity player;
    private Entity entity;

    public AttackEntityEvent(PlayerEntity player, Entity entity) {
        this.player = player;
        this. entity = entity;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public Entity getEntity() {
        return entity;
    }
}
