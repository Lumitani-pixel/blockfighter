package net.normalv.pathing.goal;

import net.minecraft.entity.Entity;

public class EntityGoal extends Goal{
    private Entity entity;

    public EntityGoal(Entity entity) {
        super(entity.getBlockPos());
        this.entity = entity;
    }

    @Override
    public void update() {
        blockPos = entity.getBlockPos();
        vec3d = entity.getEntityPos();
    }

    public Entity getEntity() {
        return entity;
    }
}
