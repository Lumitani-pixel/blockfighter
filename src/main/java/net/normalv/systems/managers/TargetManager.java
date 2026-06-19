package net.normalv.systems.managers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.normalv.event.events.impl.TargetChangeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TargetManager extends Manager {
    private LivingEntity currentTarget;
    private TargetSorting targetSorting = TargetSorting.DISTANCE;

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

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player || !(entity instanceof LivingEntity living) || living.isDeadOrDying()) continue;

            boolean allowed = false;

            if (allowPlayer && entity instanceof Player) {
                allowed = true;
            } else if (allowHostiles && entity instanceof Monster) {
                allowed = true;
            } else if (allowAnimals && entity instanceof Animal) {
                allowed = true;
            }

            if (!allowed) continue;

            targets.add(living);
        }

        switch (sorting) {
            case HEALTH -> targets.sort(Comparator.comparingDouble(LivingEntity::getHealth));
            case DISTANCE -> targets.sort(Comparator.comparingDouble(entity -> mc.player.distanceToSqr(entity)));
        }

        return new ArrayList<>(targets);
    }

    /**
     * Called every tick to update the current target.
     */
    public void update() {
        if (mc.level == null || mc.player == null) return;

        List<Entity> targets = getEntities(targetSorting);

        if (!targets.isEmpty()) {
            if(currentTarget == targets.getFirst()) return;

            EVENT_BUS.post(new TargetChangeEvent(currentTarget, targets.getFirst()));
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