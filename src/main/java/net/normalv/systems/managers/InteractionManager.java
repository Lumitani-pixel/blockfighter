package net.normalv.systems.managers;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class InteractionManager extends Manager{
    private boolean simulateKeyPresses = false; // ONLY WORKS IF mc.screen == null and the game isn't paused otherwise minecraft will catch input

    public void attack(Entity entity) {
        if(simulateKeyPresses) {
            useKey(mc.options.keyAttack);
        } else {
            mc.gameMode.attack(mc.player, entity);
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }

    public void singleUseItem() {
        if(simulateKeyPresses) {
            useKey(mc.options.keyUse);
        } else {
            mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
