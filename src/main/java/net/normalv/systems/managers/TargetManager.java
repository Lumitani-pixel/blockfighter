package net.normalv.systems.managers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class TargetManager extends Manager{
    private Entity currentTarget;
    private TargetSorting targetSorting = TargetSorting.DISTANCE;
    private List<Class<Entity>> permittedEntities = new ArrayList<>();

    public void update() {
        mc.world.getEntities().forEach(entity -> {
            if(currentTarget==null) currentTarget=entity;

            else if(targetSorting==TargetSorting.DISTANCE
                    && (permittedEntities.contains(entity.getClass()) || permittedEntities.contains(LivingEntity.class))
                    && mc.player.distanceTo(entity) < mc.player.distanceTo(currentTarget)) currentTarget = entity;

            else if(targetSorting==TargetSorting.HEALTH
                    && (permittedEntities.contains(entity.getClass()) || permittedEntities.contains(LivingEntity.class))
                    && ((LivingEntity) entity).getHealth() < currentTarget.getEntity().getHealth()) currentTarget = entity;
        });
    }

    public float getHealthDifference(Entity target) {
        return mc.player.getHealth()-target.getEntity().getHealth();
    }

    public void setTargetSorting(TargetSorting targetSorting) {
        this.targetSorting = targetSorting;
    }

    public void setPermittedEntities(List<Class<Entity>> permittedEntities) {
        this.permittedEntities = permittedEntities;
    }

    public void addToPermittedEntityList(Class<Entity> entityClass) {
        permittedEntities.add(entityClass);
    }

    public void removeFromPermittedEntityList(Class<Entity> entityClass) {
        permittedEntities.remove(entityClass);
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }

    public enum TargetSorting{
        DISTANCE,
        HEALTH
    }
}
