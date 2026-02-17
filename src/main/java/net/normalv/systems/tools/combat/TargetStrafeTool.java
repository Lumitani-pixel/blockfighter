package net.normalv.systems.tools.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import java.util.Random;

public class TargetStrafeTool extends Tool {
    private final Random random = new Random();
    private boolean strafeLeft = true;
    private boolean allowJump = true;
    private int switchTicks = 0;

    private LivingEntity target;

    public TargetStrafeTool() {
        super("TargetStrafe", "Circles target", Category.COMBAT);
    }

    @Override
    public void onTick() {
        target = BlockFighter.targetManager.getCurrentTarget();
        if (target == null) return;

        double dist = mc.player.distanceTo(target);
        if (dist > 4.2) return;
        else if(dist < BlockFighter.fightBot.getMaxReach()-0.1) mc.options.backKey.setPressed(true);
        else if(mc.options.backKey.isPressed()) mc.options.backKey.setPressed(false);

        // Randomly swap strafe direction
        if (++switchTicks > 20 + random.nextInt(20)) {
            strafeLeft = !strafeLeft;
            switchTicks = 0;
        }

        mc.options.leftKey.setPressed(strafeLeft);
        mc.options.rightKey.setPressed(!strafeLeft);
        mc.options.forwardKey.setPressed(true);

        // Occasional hop for crit chaining
        if (allowJump && mc.player.isOnGround() && random.nextFloat() < 0.02f) {
            mc.options.jumpKey.setPressed(true);
        }
    }

    @Override
    public void onDisabled() {
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
    }
}