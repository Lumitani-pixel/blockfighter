package net.normalv.util.player.inventory.strategy;

import net.normalv.util.player.inventory.Result;

public interface SwapStrategy {
    boolean swap(Result result);

    boolean swapBack(int last, Result result);
}
