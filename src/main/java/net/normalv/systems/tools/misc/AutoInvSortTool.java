package net.normalv.systems.tools.misc;

import net.minecraft.screen.slot.SlotActionType;
import net.normalv.systems.tools.Tool;
import net.normalv.util.player.inventory.SlotUtils;
import net.normalv.util.player.inventory.sorting.InventorySortPlanner;

import java.util.List;

public class AutoInvSortTool extends Tool {

    private final InventorySortPlanner planner = new InventorySortPlanner();
    private int rerunCounter = 5;

    public AutoInvSortTool() {
        super("AutoInvSort", "Sorts inventory for bot to properly use", Category.MISC);
    }

    @Override
    public void onEnabled() {
        rerunCounter = 5;
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        List<InventorySortPlanner.PlannedMove> moves = planner.plan();

        if (moves.isEmpty()) {
            disable();
            return;
        }

        moves.forEach(move -> {
            if(move.fromSlot() == move.toSlot()) return;

            if(move.toSlot() >= 36 && move.toSlot() <= 39)  mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(move.fromSlot()), 0, SlotActionType.QUICK_MOVE, mc.player);
            else {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(move.fromSlot()), 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(move.toSlot()), 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(move.fromSlot()), 0, SlotActionType.PICKUP, mc.player);
            }
        });

        if(--rerunCounter > 0) return;
        disable();
    }
}