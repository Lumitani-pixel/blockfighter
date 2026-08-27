package net.normalv.systems.tools.combat;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.player.AutoWindChargeTool;

import java.util.Random;

public class TargetStrafeTool extends Tool {
    private final Random random = new Random();
    private boolean lookAtTarget = true;
    private boolean strafeLeft = true;
    private boolean allowJump = true;
    private int minTicksToSwitch = 10;
    private int maxTicksToSwitch = 25;
    private int switchTicks = 0;

    private LivingEntity target;

    public TargetStrafeTool() {
        super("TargetStrafe", "Circles target", Category.COMBAT);
    }

    @Override
    public void onTick() {
        target = BlockFighter.targetManager.getCurrentTarget();
        if (target == null) return;

        if (!BlockFighter.playerManager.isWithinHitboxRangeHorizontal(target, 4.2)) return;
        else if(BlockFighter.playerManager.isWithinHitboxRangeHorizontal(target, BlockFighter.fightBot.getMaxReach())){
            mc.options.keyDown.setDown(true);
            mc.options.keyUp.setDown(false);
        }
        else {
            mc.options.keyDown.setDown(false);
            mc.options.keyUp.setDown(true);
        }

        if(lookAtTarget) mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());

        // Randomly swap strafe direction
        if (++switchTicks > 20 + random.nextInt(minTicksToSwitch, maxTicksToSwitch)) {
            strafeLeft = !strafeLeft;
            switchTicks = 0;
        }

        if(BlockFighter.fightBot.isMacing()) {
            mc.options.keyRight.setDown(false);
            mc.options.keyLeft.setDown(false);
        }else {
            mc.options.keyLeft.setDown(strafeLeft);
            mc.options.keyRight.setDown(!strafeLeft);
            mc.options.keyUp.setDown(true);
        }

        // Occasional hop for crit chaining
        if (allowJump && mc.player.onGround() && random.nextBoolean()) {
            mc.options.keyJump.setDown(true);
        }
    }

    @Override
    public void onDisabled() {
        mc.options.keyLeft.setDown(false);
        mc.options.keyRight.setDown(false);
    }
}