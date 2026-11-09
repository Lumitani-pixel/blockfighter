package net.normalv.systems.managers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.normalv.BlockFighter;
import net.normalv.util.MathUtil;
import net.normalv.util.player.inventory.SlotUtils;

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

    public boolean isEatingFood() {
        return mc.player.isUsingItem() && isFood(mc.player.getActiveItem().getItem());
    }

    public boolean isViewBLocked(Entity entity) {
        return mc.getCameraEntity().pick(Math.sqrt(mc.player.distanceToSqr(entity)) / 2, 1.0f, false).getType() == HitResult.Type.BLOCK;
    }

    public void switchSlot(int to) {
        int current = mc.player.getInventory().getSelectedSlot();
        if (current == to) return;
        mc.player.getInventory().setSelectedSlot(to);
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(to));
    }

    public boolean isWithinHitboxRange(Entity entity, double range) {
        return mc.player.getEyePosition().distanceToSqr(MathUtil.clamp(mc.player.getEyePosition(), entity.getBoundingBox())) <= range * range;
    }

    public boolean isWithinHitboxRangeHorizontal(Entity entity, double range) {
        AABB aabb = entity.getBoundingBox();

        double px = mc.player.getEyePosition().x();
        double pz = mc.player.getEyePosition().z();;

        double cx = Mth.clamp(px, aabb.minX, aabb.maxX);
        double cz = Mth.clamp(pz, aabb.minZ, aabb.maxZ);

        double dx = px - cx;
        double dz = pz - cz;

        return (dx * dx + dz * dz) <= (range * range);
    }

    public BlockPos getGroundPos(LivingEntity livingEntity) {
        BlockPos.MutableBlockPos pos = livingEntity.getBlockPosBelowThatAffectsMyMovement().mutable();

        while(mc.level.getBlockState(pos).isAir()) {
            pos.move(pos.getX(), pos.getY()-1, pos.getZ());
        }

        return pos.immutable();
    }

    public int getDistanceToCeiling(LivingEntity entity) {
        BlockPos pos = entity.blockPosition();

        for (int y = pos.getY() + 1; y <= 256; y++) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());

            if (!mc.level.getBlockState(checkPos).isAir()) {
                return y - pos.getY();
            }
        }

        return 256; // no ceiling found
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

    public Vec3 getNearestVisiblePoint(Vec3 source, AABB box) {
        int stepsX = Math.max(1, (int) Math.ceil((box.maxX - box.minX) / 0.15));
        int stepsY = Math.max(1, (int) Math.ceil((box.maxY - box.minY) / 0.15));
        int stepsZ = Math.max(1, (int) Math.ceil((box.maxZ - box.minZ) / 0.15));

        double maxReach = BlockFighter.fightBot.getMaxReach();
        double maxReachSqr = maxReach * maxReach;

        Vec3 bestHitVec = null;
        double bestDistance = Double.POSITIVE_INFINITY;

        for (int ix = 0; ix <= stepsX; ix++) {
            double x = Mth.lerp(ix / (double) stepsX, box.minX, box.maxX);

            for (int iy = 0; iy <= stepsY; iy++) {
                double y = Mth.lerp(iy / (double) stepsY, box.minY, box.maxY);

                for (int iz = 0; iz <= stepsZ; iz++) {
                    if (ix != 0 && ix != stepsX && iy != 0 && iy != stepsY && iz != 0 && iz != stepsZ)
                        continue;

                    double z = Mth.lerp(iz / (double) stepsZ, box.minZ, box.maxZ);
                    Vec3 sample = new Vec3(x, y, z);
                    double distance = source.distanceToSqr(sample);

                    if (distance > maxReachSqr || distance >= bestDistance)
                        continue;

                    if (!isVisible(source, sample))
                        continue;

                    bestDistance = distance;
                    bestHitVec = sample;
                }
            }
        }

        return bestHitVec;
    }

    public boolean isVisible(Vec3 from, Vec3 to) {
        BlockHitResult result = mc.level.clip(
                new ClipContext(
                        from,
                        to,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        mc.player
                )
        );

        return result.getType() == HitResult.Type.MISS;
    }

    //This math looks a bit overkill I'm not sure if we need coefficient for MINECRAFT CALCULATIONS
    public float[] getBowRotationsTo(Entity entity, boolean eyeHeight) {
        float duration = (float) (mc.player.getActiveItem().getUseDuration(mc.player) - mc.player.getTicksUsingItem()) / 20.0f;
        duration = (duration * duration + duration * 2.0f) / 3.0f;

        if (duration >= 1.0f) {
            duration = 1.0f;
        }

        double duration1 = duration * 3.0f;
        double coeff = 0.05000000074505806;
        float pitch = (float) (-Math.toDegrees(calculateArc(entity, duration1, coeff, eyeHeight)));
        double ix = entity.getX() - entity.xOld;
        double iz = entity.getZ() - entity.zOld;
        double d = mc.player.distanceTo(entity);

        d -= d % 2.0;
        ix = d / 2.0 * ix * (mc.player.isSprinting() ? 1.3 : 1.1);
        iz = d / 2.0 * iz * (mc.player.isSprinting() ? 1.3 : 1.1);

        float yaw = (float) Math.toDegrees(Math.atan2(entity.getZ() + iz - mc.player.getZ(), entity.getX() + ix - mc.player.getX())) - 90.0f;

        return new float[]{yaw, pitch};
    }

    public float calculateArc(Entity target, double duration, double coeff, boolean eyeHeight) {
        double yArc = target.getY() + (double) (target.getEyeHeight() / (eyeHeight ? 1.0f : 2.0f)) - (mc.player.getY() + (double) mc.player.getEyeHeight());
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

    public boolean hasFoodItem() {
        for (int i = 0; i <= SlotUtils.MAIN_END; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (isFood(stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldHeal() {
        if(!hasFoodItem()) return false;
        if(mc.player.getHealth()<=minHealth ||
                (BlockFighter.fightBot.getTarget() instanceof Player player && player.getActiveItem().is(Items.GOLDEN_APPLE) && player.isUsingItem() && mc.player.getHealth() < secondaryHealth)) return true;
        return false;
    }

    public void setMinHealth(float newMin) {
        minHealth = newMin;
    }
}
