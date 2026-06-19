package net.normalv.util.player.inventory.strategy;

import net.minecraft.world.inventory.ContainerInput;
import net.normalv.util.player.inventory.InventoryUtils;
import net.normalv.util.player.inventory.Result;
import net.normalv.util.player.inventory.ResultType;

public final class InventoryStrategy implements SwapStrategy {
    public static final InventoryStrategy INSTANCE = new InventoryStrategy();

    private InventoryStrategy() {
    }

    @Override
    public boolean swap(Result result) {
        if (result.type() != ResultType.INVENTORY && result.type() != ResultType.HOTBAR)
            return false;
        int slot = inventorySlot(result);
        InventoryUtils.click(slot, InventoryUtils.selected(), ContainerInput.SWAP);
        return true;
    }

    @Override
    public boolean swapBack(int last, Result result) {
        return swap(result);
    }

    private static int inventorySlot(Result result) {
        return result.type() == ResultType.HOTBAR ? result.slot() + 36 : result.slot();
    }
}
