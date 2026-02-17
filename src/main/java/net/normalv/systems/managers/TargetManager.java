package net.normalv.systems.managers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class TargetManager extends Manager {
    private LivingEntity currentTarget;
    private TargetSorting targetSorting = TargetSorting.DISTANCE;
    private List<Class<? extends Entity>> permittedEntities = new ArrayList<>();

    public boolean allowPlayer = true;
    public boolean allowHostiles = true;
    public boolean allowAnimals = false;

    /**
     * Gets all allowed targets and returns them in a list
     * @param sorting What to sort by
     * @return List of targets sorted by TargetSorting
     */
    public List<Entity> getEntities(TargetSorting sorting) {
        List<LivingEntity> targets = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!(entity instanceof LivingEntity living)) continue;

            boolean allowed = false;

            if (allowPlayer && entity instanceof PlayerEntity) {
                allowed = true;
            } else if (allowHostiles && entity instanceof HostileEntity) {
                allowed = true;
            } else if (allowAnimals && entity instanceof AnimalEntity) {
                allowed = true;
            }

            if (!allowed) continue;

            targets.add(living);
        }

        switch (sorting) {
            case HEALTH -> targets.sort(Comparator.comparingDouble(LivingEntity::getHealth));
            case DISTANCE -> targets.sort(Comparator.comparingDouble(entity -> mc.player.squaredDistanceTo(entity)));
        }

        return new ArrayList<>(targets);
    }

    /**
     * Called every tick to update the current target.
     */
    public void update() {
        if (mc.world == null || mc.player == null) return;

        List<Entity> targets = getEntities(targetSorting);

        if (!targets.isEmpty()) {
            currentTarget = (LivingEntity) targets.getFirst();
        } else {
            currentTarget = null;
        }
    }

    public LivingEntity getCurrentTarget() {
        return currentTarget;
    }

    public void setTargetSorting(TargetSorting targetSorting) {
        this.targetSorting = targetSorting;
    }

    public enum TargetSorting {
        DISTANCE,
        HEALTH
    }
}