package net.normalv.systems.fightbot;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.event.events.impl.AttackBlockEvent;
import net.normalv.event.events.impl.AttackEntityEvent;
import net.normalv.systems.fightbot.pathing.PathingHelper;
import net.normalv.systems.tools.client.HudTool;
import net.normalv.systems.tools.client.SoundTool;
import net.normalv.systems.tools.combat.*;
import net.normalv.systems.tools.misc.AutoInvSortTool;
import net.normalv.systems.tools.player.AntiWebTool;
import net.normalv.systems.tools.render.TargetHudTool;
import net.normalv.util.Util;

import java.util.Random;

//TODO: Implement POST movementpacket fixes so bot can play on real server without flags (movement packets are always the last ones send in vanilla each tick)
public class FightBot implements Util {
    private Entity target;
    private double maxReach = 3.0;
    private boolean enabled = false;
    private boolean healing = false;

    public static final int SWORD_SLOT = 0;
    public static final int AXE_SLOT = 1;
    public static final int BOW_SLOT = 4;
    public static final int WEB_SLOT = 5;
    public static final int WATER_SLOT = 6;
    public static final int GAPPLE_SLOT = 8;

    public AuraTool auraTool;
    public AutoShieldTool autoShieldTool;
    public AutoBowTool autoBowTool;
    public AutoWebTool autoWebTool;
    public AntiWebTool antiWebTool;
    public TargetStrafeTool targetStrafeTool;
    public AutoInvSortTool autoInvSortTool;

    private PathingHelper pathingHelper = new PathingHelper();
    private FightState state = FightState.IDLE;

    private Random random = new Random();

    private int ticksTillInventoryRefresh = 500;

    public FightBot() {
        EVENT_BUS.register(this);
    }

    public void onTick() {
        if (!enabled) return;

        updateTarget();
        if (target == null) {
            state = FightState.IDLE;
            return;
        }

        if(--ticksTillInventoryRefresh <= 0) {
            if(!autoInvSortTool.isEnabled()) autoInvSortTool.enable();
            ticksTillInventoryRefresh = 500;
        }

        updateState();
        tickState();
    }

    private void updateTarget() {
        if (target == null || !target.isAlive()) {
            target = BlockFighter.targetManager.getCurrentTarget();
        }
    }

    private void updateState() {
        if (BlockFighter.playerManager.shouldHeal()) {
            state = FightState.HEALING;
            return;
        }

        if (mc.player.distanceTo(target) > maxReach && target.getEntity().getHealth() > 10.0f) {
            state = FightState.CHASING;
            return;
        }

        state = FightState.ATTACKING;
    }

    private void tickState() {
        switch (state) {
            case HEALING -> tickHealing();
            case CHASING -> tickChasing();
            case ATTACKING -> tickCombat();
            case RUNNING -> tickRunning();
            case IDLE -> pathingHelper.stopPathing();
        }
    }

    private void tickHealing() {
        pathingHelper.stopPathing();

        disableAllCombatModules();

        if (BlockFighter.playerManager.isBlocking(mc.player)) {
            mc.interactionManager.stopUsingItem(mc.player);
        }

        if(mc.player.getInventory().getSelectedSlot() != GAPPLE_SLOT) BlockFighter.playerManager.switchSlot(GAPPLE_SLOT);

        if (!BlockFighter.playerManager.isEatingGapple()) {
            if(mc.player.distanceTo(target) < maxReach) {
                mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                mc.interactionManager.attackEntity(mc.player, target);
            }
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePos(), target.getEyePos());
        rotation[0] = BlockFighter.playerManager.wrapYaw(rotation[0] + 180 + random.nextFloat(-20f, 20f));

        mc.player.setYaw(rotation[0]);
        mc.player.setPitch(rotation[1]);
        mc.player.setHeadYaw(rotation[0]);
        mc.options.sprintKey.setPressed(true);
        mc.options.forwardKey.setPressed(true);
        mc.options.jumpKey.setPressed(true);
        mc.options.backKey.setPressed(false);
    }

    private void tickChasing() {
        if(autoBowTool.isEnabled()) autoBowTool.disable();
        pathingHelper.goToEntity(target);
    }

    private void tickRunning() {
    }

    private void tickCombat() {
        pathingHelper.stopPathing();

        if(mc.player.distanceTo(target) > maxReach) {
            if(!autoBowTool.isEnabled()) autoBowTool.enable();
            if(auraTool.isEnabled()) auraTool.disable();
            if(targetStrafeTool.isEnabled()) targetStrafeTool.disable();
            if(autoShieldTool.isEnabled() && BlockFighter.playerManager.isBlocking((PlayerEntity) target)) autoShieldTool.disable();

            return;
        }

        if(autoBowTool.isEnabled()) autoBowTool.disable();
        if(!auraTool.isEnabled()) auraTool.enable();
        if(!targetStrafeTool.isEnabled()) targetStrafeTool.enable();
        if(!autoShieldTool.isEnabled() && BlockFighter.playerManager.isBlocking((PlayerEntity) target)) autoShieldTool.enable();
        if(!autoWebTool.isEnabled()) autoWebTool.enable();
        if(!antiWebTool.isEnabled()) antiWebTool.enable();
    }

    private void disableAllCombatModules() {
        if(autoBowTool.isEnabled()) autoBowTool.disable();
        if(auraTool.isEnabled()) auraTool.disable();
        if(targetStrafeTool.isEnabled()) targetStrafeTool.disable();
        if(autoShieldTool.isEnabled() && BlockFighter.playerManager.isBlocking((PlayerEntity) target)) autoShieldTool.disable();
        if(autoWebTool.isEnabled()) autoWebTool.disable();
        if(antiWebTool.isEnabled()) antiWebTool.disable();
    }

    private void releaseAllKeys() {
        mc.options.backKey.setPressed(false);
        mc.options.forwardKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
    }

    public void onAttackBlock(AttackBlockEvent event) {
        if(!enabled) return;
    }

    public void onAttackEntity(AttackEntityEvent event) {
        if(!enabled) return;
    }

    private void onEnable() {
        auraTool = BlockFighter.toolManager.getToolByClass(AuraTool.class);
        autoShieldTool = BlockFighter.toolManager.getToolByClass(AutoShieldTool.class);
        autoBowTool = BlockFighter.toolManager.getToolByClass(AutoBowTool.class);
        antiWebTool = BlockFighter.toolManager.getToolByClass(AntiWebTool.class);
        autoWebTool = BlockFighter.toolManager.getToolByClass(AutoWebTool.class);
        targetStrafeTool = BlockFighter.toolManager.getToolByClass(TargetStrafeTool.class);
        autoInvSortTool = BlockFighter.toolManager.getToolByClass(AutoInvSortTool.class);

        if(!autoInvSortTool.isEnabled()) autoInvSortTool.enable();

        BlockFighter.toolManager.getToolByClass(HudTool.class).enable();
        BlockFighter.toolManager.getToolByClass(TargetHudTool.class).enable();
        BlockFighter.toolManager.getToolByClass(SoundTool.class).enable();
    }

    private void onDisable() {
        pathingHelper.stopPathing();
        disableAllCombatModules();
        releaseAllKeys();
    }

    private void enable(){
        BlockFighter.textManager.sendTextClientSide(Text.literal("FightBot enabled"));
        enabled = true;
        onEnable();
    }

    private void disable() {
        BlockFighter.textManager.sendTextClientSide(Text.literal("FightBot disabled"));
        enabled = false;
        onDisable();
    }

    public void toggle() {
        if(!enabled) enable();
        else disable();
    }

    public boolean isHealing() {
        return healing;
    }

    public boolean isEnabled() {
        return enabled;
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

    public enum FightState {
        IDLE,
        HEALING,
        CHASING,
        ATTACKING,
        RUNNING
    }
}
