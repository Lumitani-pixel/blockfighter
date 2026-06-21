package net.normalv.systems.tools.player;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.normalv.systems.tools.Tool;

public class AutoEatTool extends Tool {

    private static final int EAT_SLOT = 8;

    public AutoEatTool() {
        super("AutoEat", "Automatically eats food", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.gameMode == null) {
            return;
        }

        // Could make both values configurable
        if (mc.player.getHealth() >= 19.0F) {
            stopEating();
            return;
        }

        if (mc.player.getFoodData().getFoodLevel() >= 20) {
            stopEating();
            return;
        }

        ItemStack stack = mc.player.getInventory().getItem(EAT_SLOT);

        if (!isFood(stack.getItem())) {
            int foodSlot = findFoodSlot();

            if (foodSlot == -1) {
                return;
            }

            moveToEatSlot(foodSlot);
            return; // wait one tick after inventory action
        }

        mc.player.getInventory().setSelectedSlot(EAT_SLOT);
        startEating();
    }

    private void moveToEatSlot(int inventorySlot) {
        // Already in target slot
        if (inventorySlot == EAT_SLOT) {
            return;
        }

        int containerSlot = inventorySlot;

        // Convert hotbar index to container slot index
        if (inventorySlot < 9) {
            containerSlot = 36 + inventorySlot;
        }

        mc.gameMode.handleContainerInput(
                mc.player.containerMenu.containerId,
                containerSlot,
                EAT_SLOT,
                ContainerInput.SWAP,
                mc.player
        );
    }

    private int findFoodSlot() {
        Inventory inv = mc.player.getInventory();

        int bestSlot = -1;
        int bestFoodValue = -1;

        for (int i = 0; i < inv.getNonEquipmentItems().size(); i++) {
            ItemStack stack = inv.getNonEquipmentItems().get(i);

            if (!isFood(stack.getItem())) {
                continue;
            }

            FoodProperties food = stack.get(DataComponents.FOOD);

            if (food == null) {
                continue;
            }

            int nutrition = food.nutrition();

            if (nutrition > bestFoodValue) {
                bestFoodValue = nutrition;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    private void startEating() {
        mc.options.keyUse.setDown(true);
        mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
    }

    private void stopEating() {
        mc.options.keyUse.setDown(false);
        mc.gameMode.releaseUsingItem(mc.player);
    }
}
