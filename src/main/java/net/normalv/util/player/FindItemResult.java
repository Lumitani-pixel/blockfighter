package net.normalv.util.player;

import net.minecraft.item.ItemStack;

public class FindItemResult {
    private int slot;
    private ItemStack itemStack;

    public FindItemResult(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean wasFound() {
        return !itemStack.isEmpty();
    }
}
