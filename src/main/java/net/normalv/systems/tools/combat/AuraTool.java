package net.normalv.systems.tools.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.AXE_SLOT;
import static net.normalv.systems.fightbot.FightBot.SWORD_SLOT;

public class AuraTool extends Tool {
    private Entity target;

    public AuraTool() {
        super("Aura", "Hits enemies", Category.COMBAT);
    }

    @Override
    public void onTick() {
        target = BlockFighter.targetManager.getCurrentTarget();
        if (target == null || BlockFighter.fightBot.isHealing()) return;

        double maxReach = BlockFighter.fightBot.getMaxReach();

        if (BlockFighter.playerManager.isEatingGapple()) {
            mc.player.stopUsingItem();
            BlockFighter.playerManager.switchSlot(SWORD_SLOT);
        }

        if (mc.player.distanceTo(target) > maxReach) return;

        if(!mc.options.jumpKey.isPressed()) mc.options.jumpKey.setPressed(true);

        if (target instanceof PlayerEntity targetPlayer
                && BlockFighter.playerManager.isBlocking(targetPlayer)) {
            handleShieldBreak(target);
            return;
        }

        BlockFighter.playerManager.switchSlot(SWORD_SLOT);

        if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
            BlockFighter.playerManager.lookAt(target);
            mc.interactionManager.attackEntity(mc.player, target);
        }
    }

    private void handleShieldBreak(Entity target) {
        mc.player.stopUsingItem();

        BlockFighter.playerManager.switchSlot(AXE_SLOT);
        BlockFighter.playerManager.lookAt(target);
        mc.interactionManager.attackEntity(mc.player, target);
    }
}
