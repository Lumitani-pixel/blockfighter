package net.normalv.util.player.inventory;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.normalv.util.Util;
import net.normalv.util.player.inventory.strategy.HoldingStrategy;
import net.normalv.util.player.inventory.strategy.HotbarStrategy;
import net.normalv.util.player.inventory.strategy.InventoryStrategy;
import net.normalv.util.player.inventory.strategy.SwapStrategy;

import java.util.EnumSet;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class InventoryUtils implements Util {
    static final Result NONE = new Result(-1, ItemStack.EMPTY, ResultType.NONE);

    public static final EnumSet<ResultType> HOTBAR_SCOPE = EnumSet.of(ResultType.OFFHAND, ResultType.HOTBAR);
    public static final EnumSet<ResultType> INVENTORY_SCOPE = EnumSet.of(ResultType.OFFHAND, ResultType.INVENTORY);
    public static final EnumSet<ResultType> FULL_SCOPE = EnumSet.of(ResultType.OFFHAND, ResultType.HOTBAR, ResultType.INVENTORY);

    private static final List<SwapStrategy> STRATEGIES = List.of(
            HoldingStrategy.INSTANCE,
            HotbarStrategy.INSTANCE,
            InventoryStrategy.INSTANCE
    );


    private InventoryUtils() {
        throw new AssertionError();
    }

    public static ItemStack cursor() {
        return mc.player.containerMenu.getCarried();
    }

    public static int selected() {
        return mc.player.getInventory().getSelectedSlot();
    }

    public static void click(int slot, int button, ContainerInput type) {
        int id = mc.player.containerMenu.containerId;
        mc.gameMode.handleContainerInput(id, slot, button, type, mc.player);
    }

    public static void withSwap(Result result, Runnable action) {
        withSwap(result, r -> action.run());
    }

    public static void withSwap(Result result, Consumer<Result> action) {
        int lastSlot = selected();
        if (InventoryUtils.swap(result)) {
            action.accept(result);
            InventoryUtils.swapBack(result, lastSlot);
        }
    }

    public static void swap(int to) {
        if (to < 0 || to > 8) return;
        mc.player.getInventory().setSelectedSlot(to);
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(to));
    }

    public static boolean swap(Result result) {
        for (SwapStrategy strategy : STRATEGIES) {
            if (strategy.swap(result))
                return true;
        }
        return false;
    }

    public static boolean swapBack(Result result, int last) {
        for (SwapStrategy strategy : STRATEGIES) {
            if (strategy.swapBack(last, result))
                return true;
        }
        return false;
    }

    public static Result find(Item target, EnumSet<ResultType> scopes) {
        return find(stack -> stack.is(target), scopes);
    }

    public static Result find(Predicate<ItemStack> predicate, EnumSet<ResultType> scopes) {
        return find((item, scope) -> scopes.contains(scope) && predicate.test(item));
    }

    public static Result find(BiPredicate<ItemStack, ResultType> predicate) {
        ItemStack offhand = mc.player.getOffhandItem();
        if (predicate.test(offhand, ResultType.OFFHAND)) {
            return Result.fromOffhand(offhand);
        }

        for (int i = 0; i < 36; i++) {
            ItemStack item = mc.player.getInventory().getItem(i);
            ResultType type = i < 9 ? ResultType.HOTBAR : ResultType.INVENTORY;
            if (predicate.test(item, type)) {
                return new Result(i, item, type);
            }
        }

        return NONE;
    }

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
        SPEAR,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        MISC
    }
}
