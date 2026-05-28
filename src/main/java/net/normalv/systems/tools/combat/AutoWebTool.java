package net.normalv.systems.tools.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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
        if(!mc.player.getInventory().getItem(WEB_SLOT).is(Items.COBWEB)) return;

        target = BlockFighter.fightBot.getTarget();
        BlockPos targetBlockPos = target.getBlockPosBelowThatAffectsMyMovement();

        if(target == null ||
                !target.onGround() ||
                !mc.level.getBlockState(targetBlockPos.below()).isSolid() ||
                mc.player.distanceTo(target) > BlockFighter.fightBot.getMaxReach() ||
                mc.level.getBlockState(targetBlockPos).is(Blocks.COBWEB) ||
                mc.level.getBlockState(targetBlockPos).is(Blocks.WATER) ||
                mc.level.getBlockState(targetBlockPos.above()).is(Blocks.WATER) ||
                (mc.player.getBlockPosBelowThatAffectsMyMovement().getX() == targetBlockPos.getX() && mc.player.getBlockPosBelowThatAffectsMyMovement().getZ() == targetBlockPos.getZ())) return;

        if(mc.player.getInventory().getSelectedSlot() != WEB_SLOT) BlockFighter.playerManager.switchSlot(WEB_SLOT);

        BlockPos below = target.getBlockPosBelowThatAffectsMyMovement().below();
        Vec3 hitPos = Vec3.atCenterOf(below).add(0, 0.5, 0);

        BlockHitResult hit = new BlockHitResult(
                hitPos,
                Direction.UP,
                below,
                false
        );

        float[] rotation = BlockFighter.playerManager.calcAngle(mc.player.getEyePosition(), hitPos);
        mc.player.setYRot(rotation[0]);
        mc.player.setXRot(rotation[1]);
        mc.player.setYHeadRot(rotation[0]);

        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
        mc.player.swing(InteractionHand.MAIN_HAND);
    }
}