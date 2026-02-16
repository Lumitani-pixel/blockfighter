package net.normalv.util.player.inventory.sorting;

import net.normalv.util.player.inventory.InventoryUtils.SlotAssignment;
import net.normalv.util.player.inventory.InventoryUtils.SlotRoles;

import java.util.List;

public class InventoryLayouts {
    public static final List<SlotAssignment> DEFAULT_LAYOUT = List.of(
            new SlotAssignment(SlotRoles.SWORD, 0),
            new SlotAssignment(SlotRoles.AXE, 1),
            new SlotAssignment(SlotRoles.MACE, 2),
            new SlotAssignment(SlotRoles.WIND_CHARGE, 3),
            new SlotAssignment(SlotRoles.BOW, 4),
            new SlotAssignment(SlotRoles.WEB, 5),
            new SlotAssignment(SlotRoles.WATER, 6),
            new SlotAssignment(SlotRoles.GAPPLE, 8),
            new SlotAssignment(SlotRoles.SHIELD, 40),
            new SlotAssignment(SlotRoles.HELMET, 39),
            new SlotAssignment(SlotRoles.CHESTPLATE, 38),
            new SlotAssignment(SlotRoles.LEGGINGS, 37),
            new SlotAssignment(SlotRoles.BOOTS, 36)
    );
}
