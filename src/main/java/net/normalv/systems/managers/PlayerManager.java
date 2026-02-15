package net.normalv.systems.managers;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.normalv.BlockFighter;

public class PlayerManager extends Manager{
    private static float minHealth = 9.0f;
    private static float secondaryHealth = 12.0f;

    public boolean isBlocking(PlayerEntity player) {
        if(player == null) return false;
        return player.isUsingItem() && player.getActiveItem().isOf(Items.SHIELD);
    }

    public boolean isEatingGapple() {
        return mc.player.isUsingItem() && mc.player.getActiveItem().isOf(Items.GOLDEN_APPLE);
    }

    public void lookAt(Entity target) {
        mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
    }

    public void switchSlot(int to) {
        mc.player.getInventory().setSelectedSlot(to);
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().getSelectedSlot()));
    }

    public float getMiningSpeed(ItemStack stack, BlockState state) {
        return stack.getMiningSpeedMultiplier(state);
    }

    public static boolean isSuitableFor(ItemStack stack, BlockState state) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.isSuitableFor(state);
    }

    public static Direction getDirectionToEntity(Entity target) {
        if (mc.player == null || target == null) return Direction.NORTH;

        Vec3d diff = target.getEntityPos().subtract(mc.player.getEntityPos());

        double absX = Math.abs(diff.x);
        double absZ = Math.abs(diff.z);

        if (absX > absZ) {
            return diff.x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return diff.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    public BlockPos getOffsetBlockToEntity(Entity target, double distance) {
        Direction dirFromTarget = getDirectionToEntity(target).getOpposite();
        return target.getBlockPos().offset(dirFromTarget, (int) distance);
    }

    boolean canStandOn(World world, BlockPos pos) {
        BlockPos below = pos.down();
        return world.getBlockState(below).isSolidBlock(world, below)
                && world.getBlockState(pos).isAir()
                && world.getBlockState(pos.up()).isAir();
    }

    public float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = Math.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public float[] invertRotation(float[] rotation) {
        float invertedYaw = wrapYaw(rotation[0] + 180.0f);
        float invertedPitch = -rotation[1];

        return new float[]{invertedYaw, invertedPitch};
    }

    public float wrapYaw(float yaw) {
        yaw %= 360.0f;
        if (yaw >= 180.0f) yaw -= 360.0f;
        if (yaw < -180.0f) yaw += 360.0f;
        return yaw;
    }

    //This math looks a bit overkill I'm not sure if we need coefficient for MINECRAFT CALCULATIONS
    public float[] getBowRotationsTo(Entity entity) {
        float duration = (float) (mc.player.getActiveItem().getMaxUseTime(mc.player) - mc.player.getItemUseTime()) / 20.0f;
        duration = (duration * duration + duration * 2.0f) / 3.0f;

        if (duration >= 1.0f) {
            duration = 1.0f;
        }

        double duration1 = duration * 3.0f;
        double coeff = 0.05000000074505806;
        float pitch = (float) (-Math.toDegrees(calculateArc(entity, duration1, coeff)));
        double ix = entity.getX() - entity.lastX;
        double iz = entity.getZ() - entity.lastZ;
        double d = mc.player.distanceTo(entity);

        d -= d % 2.0;
        ix = d / 2.0 * ix * (mc.player.isSprinting() ? 1.3 : 1.1);
        iz = d / 2.0 * iz * (mc.player.isSprinting() ? 1.3 : 1.1);

        float yaw = (float) Math.toDegrees(Math.atan2(entity.getZ() + iz - mc.player.getZ(), entity.getX() + ix - mc.player.getX())) - 90.0f;

        return new float[]{yaw, pitch};
    }

    public float calculateArc(Entity target, double duration, double coeff) {
        double yArc = target.getY() + (double) (target.getStandingEyeHeight() / 2.0f) - (mc.player.getY() + (double) mc.player.getStandingEyeHeight());
        double dX = target.getX() - mc.player.getX();
        double dZ = target.getZ() - mc.player.getZ();
        double dirRoot = Math.sqrt(dX * dX + dZ * dZ);

        return calculateArc(duration, coeff, dirRoot, yArc);
    }

    public float calculateArc(double duration, double coeff, double root, double yArc) {
        double dirCoeff = coeff * (root * root);

        yArc = 2.0 * yArc * (duration * duration);
        yArc = coeff * (dirCoeff + yArc);
        yArc = Math.sqrt(duration * duration * duration * duration - yArc);
        duration = duration * duration - yArc;
        yArc = Math.atan2(duration * duration + yArc, coeff * root);
        duration = Math.atan2(duration, coeff * root);

        return (float) Math.min(yArc, duration);
    }

    public boolean shouldHeal() {
        if(mc.player.getHealth()<=minHealth ||
                (BlockFighter.fightBot.getTarget() instanceof PlayerEntity player && player.getActiveItem().isOf(Items.GOLDEN_APPLE) && player.isUsingItem() && mc.player.getHealth() < secondaryHealth)) return true;
        return false;
    }

    public void setMinHealth(float newMin) {
        minHealth = newMin;
    }
}
