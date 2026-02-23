package net.normalv.systems.fightbot;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.event.events.impl.AttackBlockEvent;
import net.normalv.event.events.impl.AttackEntityEvent;
import net.normalv.systems.fightbot.pathing.PathingHelper;
import net.normalv.systems.tools.client.HudTool;
import net.normalv.systems.tools.client.InfoHudTool;
import net.normalv.systems.tools.client.SoundTool;
import net.normalv.systems.tools.combat.*;
import net.normalv.systems.tools.misc.AutoInvSortTool;
import net.normalv.systems.tools.player.AntiWebTool;
import net.normalv.systems.tools.player.AutoWindChargeTool;
import net.normalv.systems.tools.render.TargetHudTool;
import net.normalv.util.Util;

import java.util.Random;

public class FightBot implements Util {
    private LivingEntity target;
    private double maxReach = 3.0;
    private double spearReach = 4.5;
    private boolean enabled = false;
    private boolean healing = false;
    private boolean macing = false;

    public static final int SWORD_SLOT = 0;
    public static final int AXE_SLOT = 1;
    public static final int MACE_SLOT = 2;
    public static final int WIND_CHARGE_SLOT = 3;
    public static final int BOW_SLOT = 4;
    public static final int WEB_SLOT = 5;
    public static final int WATER_SLOT = 6;
    public static final int SPEAR_SLOT = 7;
    public static final int GAPPLE_SLOT = 8;

    public AuraTool auraTool;
    public AutoShieldTool autoShieldTool;
    public AutoBowTool autoBowTool;
    public AutoWebTool autoWebTool;
    public AntiWebTool antiWebTool;
    public TargetStrafeTool targetStrafeTool;
    public AutoInvSortTool autoInvSortTool;
    public AutoWindChargeTool autoWindChargeTool;

    public InfoHudTool infoHudTool;

    private PathingHelper pathingHelper = new PathingHelper();
    public FightState state = FightState.IDLE;

    private int ticksTillInventoryRefresh = 500;

    private Random random = new Random();

    public FightBot() {
        EVENT_BUS.register(this);
    }

    public void onTick() {
        if (!enabled) return;

        updateTarget();
        if (target == null || !target.isAlive()) {
            disableAllCombatModules();
            releaseAllKeys();
            state = FightState.IDLE;
            return;
        }

        if(--ticksTillInventoryRefresh <= 0) {
            if(!autoInvSortTool.isEnabled()) autoInvSortTool.enable();
            ticksTillInventoryRefresh = 500;
        }

        // Ensure we can mace when falling a greater distance then 3 blocks
        if (!mc.player.isOnGround() && mc.player.getVelocity().y < 0 && mc.player.fallDistance > 3) {
            macing = true;
        } else {
            macing = false;
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

        if ((mc.player.getInventory().getStack(SPEAR_SLOT).isIn(ItemTags.SPEARS) && !BlockFighter.playerManager.isWithinHitboxRange(target, spearReach)) ||
                !BlockFighter.playerManager.isWithinHitboxRange(target, maxReach) && target.getEntity().getHealth() > 10.0f) {
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
            mc.options.useKey.setPressed(false);
            mc.interactionManager.stopUsingItem(mc.player);
        }

        if(mc.player.getInventory().getSelectedSlot() != GAPPLE_SLOT) BlockFighter.playerManager.switchSlot(GAPPLE_SLOT);

        if (!BlockFighter.playerManager.isEatingGapple()) {
            if(BlockFighter.playerManager.isWithinHitboxRange(target, maxReach)) {
                mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            mc.options.useKey.setPressed(true);
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
        disableAllCombatModules();

        if(!autoShieldTool.isEnabled() && (BlockFighter.playerManager.isMacing(target) || BlockFighter.playerManager.isSpearing(target))){
            if(mc.player.getInventory().getSelectedSlot() == SPEAR_SLOT) BlockFighter.playerManager.switchSlot(SWORD_SLOT);
            autoShieldTool.enable();
        }

        if(mc.player.getInventory().getStack(SPEAR_SLOT).isIn(ItemTags.SPEARS) &&
                mc.player.getInventory().getSelectedSlot() != SPEAR_SLOT &&
                !BlockFighter.playerManager.isMacing(target) &&
                !BlockFighter.playerManager.isSpearing(target)) BlockFighter.playerManager.switchSlot(SPEAR_SLOT);

        pathingHelper.goToEntity(target);
    }

    private void tickRunning() {
    }

    private void tickCombat() {
        pathingHelper.stopPathing();

        if(!BlockFighter.playerManager.isWithinHitboxRangeHorizontal(target, maxReach) && mc.player.getInventory().getStack(BOW_SLOT).isOf(Items.BOW) && mc.player.getInventory().contains(ItemTags.ARROWS)) {
            if(!autoBowTool.isEnabled()) autoBowTool.enable();
            if(auraTool.isEnabled()) auraTool.disable();
            if(targetStrafeTool.isEnabled()) targetStrafeTool.disable();
            if(autoShieldTool.isEnabled() && BlockFighter.playerManager.isBlocking(target)) autoShieldTool.disable();
            if(autoWindChargeTool.isEnabled()) autoWindChargeTool.disable();

            return;
        }

        if(mc.player.getInventory().getStack(MACE_SLOT).isOf(Items.MACE) && !autoWindChargeTool.isEnabled()) autoWindChargeTool.enable();

        if(autoBowTool.isEnabled()) autoBowTool.disable();
        if(!auraTool.isEnabled()) auraTool.enable();
        if(!targetStrafeTool.isEnabled()) targetStrafeTool.enable();
        if(!autoShieldTool.isEnabled()) autoShieldTool.enable();
        if(!autoWebTool.isEnabled()) autoWebTool.enable();
        if(!antiWebTool.isEnabled()) antiWebTool.enable();
    }

    private void disableAllCombatModules() {
        if(autoBowTool.isEnabled()) autoBowTool.disable();
        if(auraTool.isEnabled()) auraTool.disable();
        if(targetStrafeTool.isEnabled()) targetStrafeTool.disable();
        if(autoShieldTool.isEnabled() && !BlockFighter.playerManager.isMacing(target)) autoShieldTool.disable();
        if(autoWebTool.isEnabled()) autoWebTool.disable();
        if(autoWindChargeTool.isEnabled()) autoWindChargeTool.disable();

        macing = false;
    }

    public void releaseAllKeys() {
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
        autoWindChargeTool = BlockFighter.toolManager.getToolByClass(AutoWindChargeTool.class);

        infoHudTool = BlockFighter.toolManager.getToolByClass(InfoHudTool.class);

        if(!autoInvSortTool.isEnabled()) autoInvSortTool.enable();

        BlockFighter.toolManager.getToolByClass(HudTool.class).enable();
        BlockFighter.toolManager.getToolByClass(TargetHudTool.class).enable();
        BlockFighter.toolManager.getToolByClass(SoundTool.class).enable();
        infoHudTool.enable();
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

    public void setMacing(boolean macing) {
        this.macing = macing;
    }

    public boolean isHealing() {
        return healing;
    }

    public boolean isMacing() {
        return macing;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public double getMaxReach() {
        return maxReach;
    }

    public double getSpearReach() {
        return spearReach;
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
