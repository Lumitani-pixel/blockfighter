package net.normalv.event.events.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.normalv.event.events.Event;

public class AttackEntityEvent extends Event {
    private Player player;
    private Entity entity;

    public AttackEntityEvent(Player player, Entity entity) {
        this.player = player;
        this. entity = entity;
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getEntity() {
        return entity;
    }
}
