package net.normalv.systems.managers;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.normalv.BlockFighter;
import net.normalv.util.player.inventory.InventoryUtils.SlotRoles;
import net.normalv.util.player.inventory.SlotUtils;
import net.normalv.util.player.inventory.sorting.ItemRoleMap;

public class PlayerManager extends Manager{
    private static float minHealth = 9.0f;
    private static float secondaryHealth = 12.0f;

    public boolean isBlocking(LivingEntity livingEntity) {
        if(livingEntity == null) return false;
        return livingEntity.isUsingItem() && livingEntity.getActiveItem().is(Items.SHIELD);
    }

    public boolean isMacing(LivingEntity livingEntity) {
        if(livingEntity == null) return false;
        if(livingEntity.yOld - livingEntity.getY() < 0.2) return false;

        return !livingEntity.onGround() && isWithinHitboxRangeHorizontal(livingEntity, 5);
    }

    public boolean isSpearing(LivingEntity entity) {
        if (entity == null || !entity.getMainHandItem().is(ItemTags.SPEARS)) return false;

        double dx = mc.player.getX() - entity.getX();
        double dz = mc.player.getZ() - entity.getZ();
        double currentDist = Math.sqrt(dx * dx + dz * dz);

        double lastDx = mc.player.xOld - entity.xOld;
        double lastDz = mc.player.yOld - entity.yOld;
        double lastDist = Math.sqrt(lastDx * lastDx + lastDz * lastDz);

        if (currentDist >= lastDist) return false;

        double blocksMoved = lastDist - currentDist;
        double bps = blocksMoved * 20 * 5;

        return bps > 15;
    }

    public boolean isEatingGapple() {
        return mc.player.isUsingItem() && mc.player.getActiveItem().is(Items.GOLDEN_APPLE);
    }

    public boolean isUsingBow(LivingEntity livingEntity) {
        return livingEntity.getMainHandItem().is(Items.BOW) && livingEntity.isUsingItem();
    }

    public void lookAt(Entity target) {
        mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, mc.player.getEyePosition());
    }

    public boolean canHit(Entity entity) {
        Vec3 start = mc.player.getEyePosition();
        Vec3 end = entity.getBoundingBox().getCenter();

        return mc.level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                mc.player
        )).getBlockPos().equals(entity.blockPosition());
    }

    public boolean isViewBLocked(Entity entity) {
        return mc.getCameraEntity().pick(Math.sqrt(mc.player.distanceToSqr(entity)) / 2, 1.0f, false).getType() == HitResult.Type.BLOCK;
    }

    public void switchSlot(int to) {
        int current = mc.player.getInventory().getSelectedSlot();
        if (current == to) return;
        mc.player.getInventory().setSelectedSlot(to);
        mc.getConnection().send(new ServerboundSetCreativeModeSlotPacket(mc.player.getInventory().getSelectedSlot(), mc.player.getMainHandItem()));
    }

    public float getHDistanceTo(Entity entity) {
        float dx = (float) (mc.player.getX() - entity.getX());
        float dz = (float) (mc.player.getZ() - entity.getZ());
        return Mth.sqrt(dx * dx + dz * dz);
    }

    public Vec3 getHitVec(Entity entity) {
        AABB aabb = entity.getBoundingBox();

        double x = Mth.clamp(mc.player.getX(), aabb.minX, aabb.maxX);
        double y = Mth.clamp(mc.player.getY(), aabb.minY, aabb.maxY);
        double z = Mth.clamp(mc.player.getZ(), aabb.minZ, aabb.maxZ);

        return new Vec3(x, y, z);
    }

    public boolean isWithinHitboxRange(Entity entity, double range) {
        AABB aabb = entity.getBoundingBox();

        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();

        // Closest point on hitbox to player
        double cx = Mth.clamp(px, aabb.minX, aabb.maxX);
        double cy = Mth.clamp(py, aabb.minY, aabb.maxY);
        double cz = Mth.clamp(pz, aabb.minZ, aabb.maxZ);

        double dx = px - cx;
        double dy = py - cy;
        double dz = pz - cz;

        return (dx * dx + dy * dy + dz * dz) <= (range * range);
    }

    public boolean isWithinHitboxRangeHorizontal(Entity entity, double range) {
        AABB aabb = entity.getBoundingBox();

        double px = mc.player.getX();
        double pz = mc.player.getZ();;

        double cx = Mth.clamp(px, aabb.minX, aabb.maxX);
        double cz = Mth.clamp(pz, aabb.minZ, aabb.maxZ);

        double dx = px - cx;
        double dz = pz - cz;

        return (dx * dx + dz * dz) <= (range * range);
    }

    public int getDistanceToGround(LivingEntity livingEntity) {
        BlockPos pos = livingEntity.getBlockPosBelowThatAffectsMyMovement();

        int distance = 0;
        for (int y = pos.getY(); y >= 0; y--) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState state = mc.level.getBlockState(checkPos);
            if (!state.isAir()) {
                distance = pos.getY() - y;
                break;
            }
        }

        return distance;
    }

    public float getMiningSpeed(ItemStack stack, BlockState state) {
        return stack.getDestroySpeed(state);
    }

    public static boolean isSuitableFor(ItemStack stack, BlockState state) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.isCorrectToolForDrops(state);
    }

    public static Direction getDirectionToEntity(Entity target) {
        if (mc.player == null || target == null) return Direction.NORTH;

        Vec3 diff = target.position().subtract(mc.player.position());

        double absX = Math.abs(diff.x);
        double absZ = Math.abs(diff.z);

        if (absX > absZ) {
            return diff.x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return diff.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    boolean canStandOn(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).entityCanStandOn(level, below, mc.player)
                && level.getBlockState(pos).isAir()
                && level.getBlockState(pos.above()).isAir();
    }

    public float[] calcAngle(Vec3 from, Vec3 to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = Math.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) Mth.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) Mth.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
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
        float duration = (float) (mc.player.getActiveItem().getUseDuration(mc.player) - mc.player.getTicksUsingItem()) / 20.0f;
        duration = (duration * duration + duration * 2.0f) / 3.0f;

        if (duration >= 1.0f) {
            duration = 1.0f;
        }

        double duration1 = duration * 3.0f;
        double coeff = 0.05000000074505806;
        float pitch = (float) (-Math.toDegrees(calculateArc(entity, duration1, coeff)));
        double ix = entity.getX() - entity.xOld;
        double iz = entity.getZ() - entity.zOld;
        double d = mc.player.distanceTo(entity);

        d -= d % 2.0;
        ix = d / 2.0 * ix * (mc.player.isSprinting() ? 1.3 : 1.1);
        iz = d / 2.0 * iz * (mc.player.isSprinting() ? 1.3 : 1.1);

        float yaw = (float) Math.toDegrees(Math.atan2(entity.getZ() + iz - mc.player.getZ(), entity.getX() + ix - mc.player.getX())) - 90.0f;

        return new float[]{yaw, pitch};
    }

    public float calculateArc(Entity target, double duration, double coeff) {
        double yArc = target.getY() + (double) (target.getEyeHeight() / 2.0f) - (mc.player.getY() + (double) mc.player.getEyeHeight());
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

    public boolean hasGapples() {
        for (int i = 0; i <= SlotUtils.MAIN_END; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (ItemRoleMap.itemsForRole(SlotRoles.GAPPLE).contains(stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldHeal() {
        if(!hasGapples()) return false;
        if(mc.player.getHealth()<=minHealth ||
                (BlockFighter.fightBot.getTarget() instanceof Player player && player.getActiveItem().is(Items.GOLDEN_APPLE) && player.isUsingItem() && mc.player.getHealth() < secondaryHealth)) return true;
        return false;
    }

    public void setMinHealth(float newMin) {
        minHealth = newMin;
    }
}
