package net.normalv.systems.tools.combat;

import net.minecraft.world.entity.LivingEntity;
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

        if (!BlockFighter.playerManager.isWithinHitboxRange(target, 4.2)) return;
        else if(BlockFighter.playerManager.isWithinHitboxRange(target, BlockFighter.fightBot.getMaxReach() - 0.1)) mc.options.keyDown.setDown(true);
        else if(mc.options.keyDown.isDown()) mc.options.keyDown.setDown(false);

        // Randomly swap strafe direction
        if (++switchTicks > 20 + random.nextInt(20)) {
            strafeLeft = !strafeLeft;
            switchTicks = 0;
        }

        mc.options.keyLeft.setDown(strafeLeft);
        mc.options.keyRight.setDown(!strafeLeft);
        mc.options.keyUp.setDown(true);

        // Occasional hop for crit chaining
        if (allowJump && mc.player.onGround() && random.nextFloat() < 0.02f) {
            mc.options.keyJump.setDown(true);
        }
    }

    @Override
    public void onDisabled() {
        mc.options.keyLeft.setDown(false);
        mc.options.keyRight.setDown(false);
    }
}