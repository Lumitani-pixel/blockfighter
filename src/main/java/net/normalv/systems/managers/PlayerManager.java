package net.normalv.systems.managers;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.normalv.util.player.FindItemResult;
import net.normalv.util.player.SlotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerManager extends Manager{
    private static final Logger log = LoggerFactory.getLogger(PlayerManager.class);
    private static float minHealth = 5.0f;

    public FindItemResult findItem(Item item) {
        for(int i = SlotUtils.HOTBAR_START; i<SlotUtils.MAIN_END; i++) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if(itemStack.getItem()==item) {
                return new FindItemResult(i, itemStack);
            }
        }
        return null;
    }

    public int hasEmptyHotBarSlot() {
        for(int i = SlotUtils.HOTBAR_START; i<=SlotUtils.HOTBAR_END; i++) {
            if(!mc.player.getInventory().getStack(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    public boolean moveItem(int itemSlot, int targetSlot) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, targetSlot, 0, SlotActionType.PICKUP, mc.player);
        if(mc.player.getInventory().getStack(targetSlot).isEmpty()) return true;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, mc.player);
        return true;
    }

    public void shiftClickItem(int itemSlot) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, itemSlot, 1, SlotActionType.QUICK_MOVE, mc.player);
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
        return mc.player.getHealth()<=minHealth;
    }

    public void setMinHealth(float newMin) {
        minHealth = newMin;
    }
}
