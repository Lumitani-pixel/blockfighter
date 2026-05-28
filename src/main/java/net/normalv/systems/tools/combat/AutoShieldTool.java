package net.normalv.systems.tools.combat;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

public class AutoShieldTool extends Tool {
    public AutoShieldTool() {
        super("AutoShield", "Shields when needed", Category.COMBAT);
    }

    @Override
    public void onDisabled() {
        if(BlockFighter.playerManager.isBlocking(mc.player)) {
            mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
        }
    }

    @Override
    public void onTick() {
        LivingEntity target = BlockFighter.fightBot.getTarget();
        if (target == null) return;

        if (!mc.player.getInventory().getItem(40).is(Items.SHIELD)) return;

        if(BlockFighter.fightBot.shieldIsRequired()) {
            if (!mc.player.isUsingItem()) {
                mc.options.keyUse.setDown(true);
                mc.gameMode.useItem(mc.player, InteractionHand.OFF_HAND);
            }
            return;
        }
        else if (!BlockFighter.playerManager.isWithinHitboxRangeHorizontal(target, BlockFighter.fightBot.getMaxReach()+0.9) ||
                BlockFighter.playerManager.shouldHeal() ||
                (mc.player.getAttackStrengthScale(0.5f) >= 0.99f && BlockFighter.fightBot.auraTool.isEnabled()) ||
                BlockFighter.fightBot.isMacing()) {

            if(BlockFighter.playerManager.isBlocking(mc.player)) {
                mc.options.keyUse.setDown(false);
                mc.gameMode.releaseUsingItem(mc.player);
            }
            return;
        }

        if (!mc.player.isUsingItem()) {
            mc.options.keyUse.setDown(true);
            mc.gameMode.useItem(mc.player, InteractionHand.OFF_HAND);
        }
    }
}
