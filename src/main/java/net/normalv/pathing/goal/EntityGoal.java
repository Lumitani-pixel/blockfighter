package net.normalv.pathing.goal;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;

public class EntityGoal extends Goal{
    private Entity entity;

    public EntityGoal(Entity entity) {
        super(entity.getBlockPosBelowThatAffectsMyMovement());
        this.entity = entity;
    }

    @Override
    public void update() {
        blockPos = entity.getBlockPosBelowThatAffectsMyMovement();
        vec3i = new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Entity getEntity() {
        return entity;
    }
}
