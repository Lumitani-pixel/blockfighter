package net.normalv.systems.tools.misc;

import net.minecraft.screen.slot.SlotActionType;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.util.player.inventory.SlotUtils;
import net.normalv.util.player.inventory.sorting.InventorySortPlanner;

import java.util.List;

public class AutoInvSortTool extends Tool {

    private final InventorySortPlanner planner = new InventorySortPlanner();
    private int rerunCounter = 6;
    private int initialSlot;

    public AutoInvSortTool() {
        super("AutoInvSort", "Sorts inventory for bot to properly use", Category.MISC);
    }

    @Override
    public void onEnabled() {
        rerunCounter = 6;
        initialSlot = mc.player.getInventory().getSelectedSlot();
    }

    @Override
    public void onDisabled() {
        BlockFighter.playerManager.switchSlot(initialSlot);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        List<InventorySortPlanner.PlannedMove> moves = planner.plan();

        if (moves.isEmpty()) {
            disable();
            return;
        }

        BlockFighter.fightBot.releaseAllKeys();

        moves.forEach(move -> {
            if(move.fromSlot() == move.toSlot()) return;

            if(move.toSlot() >= 36 && move.toSlot() <= 40)  mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(move.fromSlot()), 0, SlotActionType.QUICK_MOVE, mc.player);
            else if(move.toSlot() < 9) {
                BlockFighter.playerManager.switchSlot(move.toSlot());
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, SlotUtils.indexToId(move.fromSlot()), mc.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, mc.player);
            }
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