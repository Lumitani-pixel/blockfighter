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

    public double getAttackDamage(ItemStack stack) {
        AttributeModifiersComponent modifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (modifiers==null) return 0.0;

        return modifiers.applyOperations(0.0, EquipmentSlot.MAINHAND);
    }

    public double getGenericProtection(ItemStack stack, EquipmentSlot slot) {
        AttributeModifiersComponent component = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if(component==null) return 0.0;

        return component.applyOperations(EntityAttributes.ARMOR.value().getDefaultValue(), slot)
                - EntityAttributes.ARMOR.value().getDefaultValue();
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

    //TODO implement yaw aiming (I'm too bad at maths)
    public boolean aim(Entity target) {
        if(!mc.player.getInventory().getSelectedStack().isOf(Items.BOW)) return false;
        float velocity = BowItem.getPullProgress(mc.player.getItemUseTime());
        Vec3d pos = target.getEntityPos();

        double relativeX = pos.x - mc.player.getX();
        double relativeY = pos.y + (target.getHeight() / 2) - mc.player.getEyeY();
        double relativeZ = pos.z - mc.player.getZ();

        double hDistance = Math.sqrt(relativeX * relativeX + relativeZ * relativeZ);
        double hDistanceSq = hDistance * hDistance;
        float g = 0.006f;
        float velocitySq = velocity * velocity;
        float pitch = (float) -Math.toDegrees(Math.atan((velocitySq - Math.sqrt(velocitySq * velocitySq - g * (g * hDistanceSq + 2 * relativeY * velocitySq))) / (g * hDistance)));

        if (Float.isNaN(pitch)) {
            return false;
        } else {
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(pos.x, pos.y, pos.z));
            mc.player.setPitch(pitch);
            return true;
        }
    }

    public boolean shouldHeal() {
        return mc.player.getHealth()<=minHealth;
    }

    public void setMinHealth(float newMin) {
        minHealth = newMin;
    }
}
