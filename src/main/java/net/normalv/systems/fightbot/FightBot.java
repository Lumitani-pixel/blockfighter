package net.normalv.systems.fightbot;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.event.events.impl.AttackBlockEvent;
import net.normalv.event.events.impl.AttackEntityEvent;
import net.normalv.systems.fightbot.pathing.PathingHelper;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.combat.AutoShieldTool;
import net.normalv.util.Util;

//TODO: REFACTOR ALL COMBAT FEATURES ITS BAD AND MESSY RIGHT NOW
public class FightBot implements Util {
    private Entity target;
    private double maxReach = 3.0;
    private boolean isEnabled = false;
    boolean isBlocking = false;
    boolean targetIsBlocking = false;

    private PathingHelper pathingHelper = new PathingHelper();

    public FightBot() {
        EVENT_BUS.register(this);
    }

    public void onTick() {
        if(target==null || !target.isAlive()) {
            target = BlockFighter.targetManager.getCurrentTarget();
            return;
        }

        isBlocking = mc.player.isUsingItem() && mc.player.getActiveItem().isOf(Items.SHIELD);
        if(target instanceof PlayerEntity targetPlayer) {
            targetIsBlocking = targetPlayer.isUsingItem() && targetPlayer.getActiveItem().isOf(Items.SHIELD);
        }

        if(BlockFighter.playerManager.shouldHeal()) {
            pathingHelper.stopPathing();

            if(isBlocking) mc.player.stopUsingItem();
            mc.player.getInventory().setSelectedSlot(8);
            if(!mc.player.isUsingItem() || !mc.player.getActiveItem().isOf(Items.GOLDEN_APPLE)) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
            mc.options.backKey.setPressed(true);
            return;
        } else if(mc.player.isUsingItem() && mc.player.getActiveItem().isOf(Items.GOLDEN_APPLE)) {
            mc.player.stopUsingItem();
            mc.player.getInventory().setSelectedSlot(0);
        }

        pathingHelper.goToEntity(target);

        if(mc.player.distanceTo(target) <= maxReach) {
            if(targetIsBlocking) {
                mc.player.stopUsingItem();

                if(mc.player.getInventory().getSelectedSlot() != 1) {
                    mc.player.getInventory().setSelectedSlot(1);
                    return;
                }

                mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                mc.interactionManager.attackEntity(mc.player, target);
                return;
            } else if(mc.player.getInventory().getSelectedSlot() != 0) {
                mc.player.getInventory().setSelectedSlot(0);
            }

            if(mc.player.getAttackCooldownProgress(0.5f) >= 1) {
                mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                mc.interactionManager.attackEntity(mc.player, target);
            }
        }
    }

    public void onAttackBlock(AttackBlockEvent event) {
        if(!isEnabled) return;
    }

    public void onAttackEntity(AttackEntityEvent event) {
        if(!isEnabled) return;
    }

    private void onEnable() {
        BlockFighter.toolManager.getToolByClass(AutoShieldTool.class).enable();
    }

    private void onDisable() {
        pathingHelper.stopPathing();
        BlockFighter.toolManager.getTools().forEach(Tool::disable);
    }

    private void enable(){
        BlockFighter.textManager.sendTextClientSide(Text.literal("FightBot enabled"));
        isEnabled = true;
        onEnable();
    }

    private void disable() {
        BlockFighter.textManager.sendTextClientSide(Text.literal("FightBot disabled"));
        isEnabled = false;
        onDisable();
    }

    public void toggle() {
        if(!isEnabled) enable();
        else disable();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Entity getTarget() {
        return target;
    }

    public double getMaxReach() {
        return maxReach;
    }

    public void setMaxReach(double maxReach) {
        this.maxReach = Math.min(maxReach, 5.9);
    }
}
