package net.normalv.systems.tools.combat;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.player.AutoWindChargeTool;

import java.util.Random;

public class TargetStrafeTool extends Tool {
    private final Random random = new Random();
    private boolean lookAtTarget = true;
    private boolean strafe = false;
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

        if(BlockFighter.playerManager.isWithinHitboxRange(target, BlockFighter.fightBot.getMaxReach())){
            mc.options.keyDown.setDown(true);
            mc.options.keyUp.setDown(false);
        }
        else {
            mc.options.keyDown.setDown(false);
            mc.options.keyUp.setDown(true);
        }

        if(lookAtTarget) mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());

        // Randomly swap strafe direction
        if (BlockFighter.playerManager.isWithinHitboxRange(target, 4.2) && ++switchTicks > 20 + random.nextInt(minTicksToSwitch, maxTicksToSwitch)) {
            strafe = true;
            strafeLeft = !strafeLeft;
            switchTicks = 0;
        }else strafe = false;

        if(BlockFighter.fightBot.isMacing() || !strafe) {
            mc.options.keyRight.setDown(false);
            mc.options.keyLeft.setDown(false);
        }else {
            mc.options.keyLeft.setDown(strafeLeft);
            mc.options.keyRight.setDown(!strafeLeft);
        }

        // Occasional hop for crit chaining
        if(!BlockFighter.playerManager.isWithinHitboxRangeHorizontal(target, 4.2)) {
            mc.options.keyJump.setDown(true);
        }
        else mc.options.keyJump.setDown(allowJump && mc.player.onGround() && random.nextBoolean());
    }

    @Override
    public void onDisabled() {
        mc.options.keyLeft.setDown(false);
        mc.options.keyRight.setDown(false);
    }
}