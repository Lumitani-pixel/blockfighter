package net.normalv.util.player.inventory.strategy;

import net.normalv.util.player.inventory.InventoryUtils;
import net.normalv.util.player.inventory.Result;
import net.normalv.util.player.inventory.ResultType;

public final class HotbarStrategy implements SwapStrategy {
    public static final HotbarStrategy INSTANCE = new HotbarStrategy();

    private HotbarStrategy() {
    }

    @Override
    public boolean swap(Result result) {
        if (result.type() == ResultType.HOTBAR) {
            InventoryUtils.swap(result.slot());
            return true;
        }
        return false;
    }

    @Override
    public boolean swapBack(int last, Result result) {
        if (result.type() == ResultType.HOTBAR) {
            InventoryUtils.swap(last);
            return true;
        }
        return false;
    }
}
