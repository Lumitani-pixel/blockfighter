package net.normalv.util.player.inventory.sorting;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.normalv.util.player.inventory.InventoryUtils;

import java.util.Set;

public class ItemRoleMap {

    public static Set<Item> itemsForRole(InventoryUtils.SlotRoles role) {
        return switch (role) {
            case SWORD -> Set.of(
                    Items.NETHERITE_SWORD,
                    Items.DIAMOND_SWORD,
                    Items.IRON_SWORD
            );
            case AXE -> Set.of(
                    Items.NETHERITE_AXE,
                    Items.DIAMOND_AXE
            );
            case GAPPLE -> Set.of(
                    Items.GOLDEN_APPLE,
                    Items.ENCHANTED_GOLDEN_APPLE
            );
            case SHIELD -> Set.of(
                    Items.SHIELD
            );
            case HELMET -> Set.of(
                    Items.NETHERITE_HELMET,
                    Items.DIAMOND_HELMET
            );
            case CHESTPLATE -> Set.of(
                    Items.NETHERITE_CHESTPLATE,
                    Items.DIAMOND_CHESTPLATE
            );
            case LEGGINGS -> Set.of(
                    Items.NETHERITE_LEGGINGS,
                    Items.DIAMOND_LEGGINGS
            );
            case BOOTS -> Set.of(
                    Items.NETHERITE_BOOTS,
                    Items.DIAMOND_BOOTS
            );
            default -> Set.of();
        };
    }
}

