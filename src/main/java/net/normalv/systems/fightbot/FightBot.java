package net.normalv.systems.fightbot;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
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
import net.normalv.systems.tools.player.AutoClutchTool;
import net.normalv.systems.tools.player.AutoEatTool;
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
    public AutoClutchTool autoClutchTool;

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
        if (!mc.player.onGround() && mc.player.getDeltaMovement().y < 0 && mc.player.fallDistance > 3) {
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

        if ((mc.player.getInventory().getItem(SPEAR_SLOT).is(ItemTags.SPEARS) && !BlockFighter.playerManager.isWithinHitboxRange(target, spearReach)) ||
                (!BlockFighter.playerManager.isWithinHitboxRangeHorizontal(target, maxReach) && target.asLivingEntity().getHealth() > 10.0f) || !mc.player.hasLineOfSight(target)) {
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
            mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
        }

        if(!BlockFighter.toolManager.getToolByClass(AutoEatTool.class).isEnabled()) BlockFighter.toolManager.getToolByClass(AutoEatTool.class).enable();

        if(BlockFighter.playerManager.isWithinHitboxRange(target, maxReach)) {
            mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND);
        }

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePosition(), target.getEyePosition());
        rotation[0] = BlockFighter.playerManager.wrapYaw(rotation[0] + 180 + random.nextFloat(-20f, 20f));

        mc.player.setYRot(rotation[0]);
        mc.player.setXRot(rotation[1]);
        mc.player.setYHeadRot(rotation[0]);
        mc.options.keySprint.setDown(true);
        mc.options.keyUp.setDown(true);
        mc.options.keyJump.setDown(true);
        mc.options.keyDown.setDown(false);
    }

    private void tickChasing() {
        disableAllCombatModules();

        if(BlockFighter.toolManager.getToolByClass(AutoEatTool.class).isEnabled()) BlockFighter.toolManager.getToolByClass(AutoEatTool.class).disable();
        if(!autoClutchTool.isEnabled()) autoClutchTool.enable();

        if(BlockFighter.playerManager.isEatingFood()) {
            if(mc.options.keyUse.isDown()) mc.options.keyUse.setDown(false);
            mc.gameMode.releaseUsingItem(mc.player);
        }

        if(!autoShieldTool.isEnabled() && shieldIsRequired()){
            BlockFighter.playerManager.switchSlot(SWORD_SLOT);
            autoShieldTool.enable();
        }

        pathingHelper.goToEntity(target);
    }

    // We dont run :). At least yet
    private void tickRunning() {
    }

    private void tickCombat() {
        pathingHelper.stopPathing();
        if(BlockFighter.toolManager.getToolByClass(AutoEatTool.class).isEnabled()) BlockFighter.toolManager.getToolByClass(AutoEatTool.class).disable();

        if(!BlockFighter.playerManager.isWithinHitboxRangeHorizontal(target, maxReach) && mc.player.getInventory().getItem(BOW_SLOT).is(Items.BOW) && mc.player.getInventory().contains(ItemTags.ARROWS) && mc.player.hasLineOfSight(target)) {
            if(!autoBowTool.isEnabled()) autoBowTool.enable();
            if(!autoClutchTool.isEnabled()) autoClutchTool.enable();
            if(auraTool.isEnabled()) auraTool.disable();
            if(targetStrafeTool.isEnabled()) targetStrafeTool.disable();
            if(autoShieldTool.isEnabled() && BlockFighter.playerManager.isBlocking(target)) autoShieldTool.disable();
            if(autoWindChargeTool.isEnabled()) autoWindChargeTool.disable();

            return;
        }

        if(mc.player.getInventory().getItem(MACE_SLOT).is(Items.MACE) && !autoWindChargeTool.isEnabled()) autoWindChargeTool.enable();

        if(autoClutchTool.isEnabled() && !autoClutchTool.placedWater) autoClutchTool.disable();
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
        if(autoShieldTool.isEnabled() && !shieldIsRequired()) autoShieldTool.disable();
        if(autoWebTool.isEnabled()) autoWebTool.disable();
        if(autoWindChargeTool.isEnabled()) autoWindChargeTool.disable();

        macing = false;
    }

    public void releaseAllKeys() {
        mc.options.keyDown.setDown(false);
        mc.options.keyUp.setDown(false);
        mc.options.keyJump.setDown(false);
        mc.options.keyLeft.setDown(false);
        mc.options.keyRight.setDown(false);
    }

    public boolean shieldIsRequired() {
        return BlockFighter.playerManager.isMacing(target) || BlockFighter.playerManager.isSpearing(target);
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
        autoClutchTool = BlockFighter.toolManager.getToolByClass(AutoClutchTool.class);

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
        BlockFighter.textManager.sendTextClientSide(Component.literal("FightBot enabled"));
        enabled = true;
        onEnable();
    }

    private void disable() {
        BlockFighter.textManager.sendTextClientSide(Component.literal("FightBot disabled"));
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
