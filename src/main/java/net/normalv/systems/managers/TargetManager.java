package net.normalv.systems.managers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class TargetManager extends Manager {
    private Entity currentTarget;
    private TargetSorting targetSorting = TargetSorting.DISTANCE;
    private final List<Class<? extends Entity>> permittedEntities = new ArrayList<>();

    /**
     * Called every tick to update the current target.
     */
    public void update() {
        if (mc.world == null || mc.player == null) return;

        List<Entity> targets = getTargets(
                this::isValidTarget,
                targetSorting,
                1
        );

        if (!targets.isEmpty()) {
            currentTarget = targets.getFirst();
        } else {
            currentTarget = null;
        }
    }

    /**
     * Builds a filtered + sorted list of valid targets.
     */
    public List<Entity> getTargets(Predicate<Entity> predicate, TargetSorting sort, int maxCount) {
        List<Entity> list = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (entity != null && predicate.test(entity)) list.add(entity);
        }

        list.sort(getComparator(sort));

        if (list.size() > maxCount) {
            list.subList(maxCount, list.size()).clear();
        }

        return list;
    }

    /**
     * Predicate that checks whether an entity is a valid target.
     */
    private boolean isValidTarget(Entity entity) {
        if (entity == mc.player) return false;
        if (!(entity instanceof LivingEntity)) return false;

        if (!isPermitted(entity)) return false;

        LivingEntity living = (LivingEntity) entity;

        if (!living.isAlive() || living.isDead() || living.getHealth() <= 0) return false;

        if (mc.player.isInRange(entity, 10.0)) return false;

        if (entity instanceof PlayerEntity player && player.getGameMode() != GameMode.SURVIVAL)
            return false;

        return true;
    }

    private boolean isPermitted(Entity entity) {
        for (Class<? extends Entity> clazz : permittedEntities) {
            if (clazz.isAssignableFrom(entity.getClass())) return true;
        }
        return false;
    }

    /**
     * Returns a Comparator based on the sorting mode.
     */
    private Comparator<Entity> getComparator(TargetSorting sort) {
        switch (sort) {
            case HEALTH:
                return Comparator.comparingDouble(e -> ((LivingEntity) e).getHealth());
            case DISTANCE:
            default:
                return Comparator.comparingDouble(mc.player::distanceTo);
        }
    }

    public boolean isBadTarget(Entity target, double range) {
        if (!(target instanceof LivingEntity living)) return true;
        return mc.player.isInRange(target, range) || !living.isAlive() || living.isDead() || living.getHealth() <= 0;
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }

    public void setTargetSorting(TargetSorting targetSorting) {
        this.targetSorting = targetSorting;
    }

    public void addToPermittedEntityList(Class<? extends Entity> entityClass) {
        permittedEntities.add(entityClass);
    }

    public void removeFromPermittedEntityList(Class<? extends Entity> entityClass) {
        permittedEntities.remove(entityClass);
    }

    public enum TargetSorting {
        DISTANCE,
        HEALTH
    }
}