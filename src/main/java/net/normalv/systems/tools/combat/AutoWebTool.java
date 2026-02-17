package net.normalv.systems.tools.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import static net.normalv.systems.fightbot.FightBot.WEB_SLOT;

public class AutoWebTool extends Tool {
    private LivingEntity target;

    public AutoWebTool() {
        super("AutoWeb", "Webs enemies when possible", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if(!mc.player.getInventory().getStack(WEB_SLOT).isOf(Items.COBWEB)) return;

        target = BlockFighter.fightBot.getTarget();
        if(target == null ||
                !target.isOnGround() ||
                mc.player.distanceTo(target) > BlockFighter.fightBot.getMaxReach() ||
                mc.world.getBlockState(target.getBlockPos()).isOf(Blocks.COBWEB) ||
                mc.world.getBlockState(target.getBlockPos()).isOf(Blocks.WATER) ||
                mc.world.getBlockState(target.getBlockPos().up()).isOf(Blocks.WATER)) return;

        if(mc.player.getInventory().getSelectedSlot() != WEB_SLOT) BlockFighter.playerManager.switchSlot(WEB_SLOT);

        BlockPos below = target.getBlockPos().down();
        Vec3d hitPos = Vec3d.ofCenter(below).add(0, 0.5, 0);

        BlockHitResult hit = new BlockHitResult(
                hitPos,
                Direction.UP,
                below,
                false
        );

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePos(), hitPos);
        mc.player.setYaw(rotation[0]);
        mc.player.setPitch(rotation[1]);
        mc.player.setHeadYaw(rotation[0]);

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
