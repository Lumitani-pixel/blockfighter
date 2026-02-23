package net.normalv.util.player.inventory.sorting;

import net.minecraft.item.ItemStack;
import net.normalv.util.player.inventory.FindItemResult;
import net.normalv.util.player.inventory.InventoryUtils.SlotAssignment;
import net.normalv.util.player.inventory.InventoryUtils.SlotRoles;
import net.normalv.util.player.inventory.SlotUtils;
import net.normalv.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventorySortPlanner implements Util {
    public record PlannedMove(int fromSlot, int toSlot) {}

    public List<PlannedMove> plan() {
        List<PlannedMove> moves = new ArrayList<>();
        Set<Integer> targetSlotsTaken = new HashSet<>();

        for (SlotAssignment assignment : InventoryLayouts.DEFAULT_LAYOUT) {
            FindItemResult found = findItemForRole(assignment.role());

            if (!found.wasFound()) continue;

            int targetSlot = assignment.slotIndex();
            if (found.getSlot() == targetSlot) continue;
            if (targetSlotsTaken.contains(found.getSlot())) continue;

            moves.add(new PlannedMove(found.getSlot(), targetSlot));
            targetSlotsTaken.add(targetSlot);
        }
        return moves;
    }

    private FindItemResult findItemForRole(SlotRoles role) {
        for (int i = 0; i <= SlotUtils.MAIN_END; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            if (ItemRoleMap.itemsForRole(role).contains(stack.getItem())) {
                return new FindItemResult(i, stack);
            }
        }
        return new FindItemResult(-1, ItemStack.EMPTY);
    }
}
