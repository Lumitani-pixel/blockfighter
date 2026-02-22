package net.normalv.systems.managers;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.normalv.BlockFighter;

public class PlayerManager extends Manager{
    private static float minHealth = 9.0f;
    private static float secondaryHealth = 12.0f;

    public boolean isBlocking(LivingEntity livingEntity) {
        if(livingEntity == null) return false;
        return livingEntity.isUsingItem() && livingEntity.getActiveItem().isOf(Items.SHIELD);
    }

    public boolean isMacing(LivingEntity livingEntity) {
        if(livingEntity == null) return false;
        return !livingEntity.isOnGround() && livingEntity.getMainHandStack().isOf(Items.MACE) && isWithinHitboxRangeHorizontal(livingEntity, 4);
    }

    public boolean isEatingGapple() {
        return mc.player.isUsingItem() && mc.player.getActiveItem().isOf(Items.GOLDEN_APPLE);
    }

    public void lookAt(Entity target) {
        mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
    }

    public void switchSlot(int to) {
        int current = mc.player.getInventory().getSelectedSlot();
        if (current == to) return;
        mc.player.getInventory().setSelectedSlot(to);
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().getSelectedSlot()));
    }

    public void switchToItem(ItemStack matching) {
        if (matching.isEmpty()) return;
        for (int i = 0; i <= 8; i++) {
            if (mc.player.getInventory().getStack(i).isOf(matching.getItem())) {
                switchSlot(i);
                return;
            }
        }
    }

    public void switchToItem(Item... items) {
        for (Item item : items) {
            for (int i = 0; i <= 8; i++) {
                if (mc.player.getInventory().getStack(i).isOf(item)) {
                    switchSlot(i);
                    return;
                }
            }
        }
    }

    public int findSlotWithItem(Item... items) {
        for (Item item : items) {
            for (int i = 0; i <= 8; i++) {
                if (mc.player.getInventory().getStack(i).isOf(item)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public float getHDistanceTo(Entity entity) {
        float dx = (float) (mc.player.getX() - entity.getX());
        float dz = (float) (mc.player.getZ() - entity.getZ());
        return MathHelper.sqrt(dx * dx + dz * dz);
    }

    public Vec3d getHitVec(Entity entity) {
        Box box = entity.getBoundingBox();

        Vec3d eyes = mc.player.getEyePos();

        double x = MathHelper.clamp(eyes.x, box.minX, box.maxX);
        double y = MathHelper.clamp(eyes.y, box.minY, box.maxY);
        double z = MathHelper.clamp(eyes.z, box.minZ, box.maxZ);

        return new Vec3d(x, y, z);
    }

    public boolean isWithinHitboxRange(Entity entity, double range) {
        Box box = entity.getBoundingBox();

        double px = mc.player.getEyePos().getX();
        double py = mc.player.getEyePos().getY();
        double pz = mc.player.getEyePos().getZ();

        // Closest point on hitbox to player
        double cx = MathHelper.clamp(px, box.minX, box.maxX);
        double cy = MathHelper.clamp(py, box.minY, box.maxY);
        double cz = MathHelper.clamp(pz, box.minZ, box.maxZ);

        double dx = px - cx;
        double dy = py - cy;
        double dz = pz - cz;

        return (dx * dx + dy * dy + dz * dz) <= (range * range);
    }

    public boolean isWithinHitboxRangeHorizontal(Entity entity, double range) {
        Box box = entity.getBoundingBox();

        double px = mc.player.getEyePos().getX();
        double pz = mc.player.getEyePos().getZ();;

        double cx = MathHelper.clamp(px, box.minX, box.maxX);
        double cz = MathHelper.clamp(pz, box.minZ, box.maxZ);

        double dx = px - cx;
        double dz = pz - cz;

        return (dx * dx + dz * dz) <= (range * range);
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
