package net.normalv.systems.managers;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
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

    public boolean shouldHeal() {
        return mc.player.getHealth()<=minHealth;
    }

    public void setMinHealth(float newMin) {
        minHealth = newMin;
    }
}
