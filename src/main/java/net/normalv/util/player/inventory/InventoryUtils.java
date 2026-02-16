package net.normalv.util.player.inventory;

public class InventoryUtils {
    public record SlotAssignment(
            SlotRoles role,
            int slotIndex
    ) {}

    public enum SlotRoles {
        SWORD,
        AXE,
        PICKAXE,
        GAPPLE,
        SHIELD,
        BLOCK,
        PEARL,
        POTION,
        FOOD,
        BOW,
        WEB,
        WATER,
        WIND_CHARGE,
        MACE,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        MISC
    }
}
