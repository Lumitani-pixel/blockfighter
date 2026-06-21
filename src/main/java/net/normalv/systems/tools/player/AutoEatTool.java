package net.normalv.systems.tools.player;

import net.minecraft.world.item.ItemStack;
import net.normalv.systems.tools.Tool;
import net.normalv.util.player.inventory.ResultType;

public class AutoEatTool extends Tool {
    public AutoEatTool() {
        super("AutoEat", "Automatically eats food", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if(mc.player.getHealth() >= 19) return;

        for (int i = 0; i < 36; i++) {
            ItemStack item = mc.player.getInventory().getItem(i);
            ResultType type = i < 9 ? ResultType.HOTBAR : ResultType.INVENTORY;
        }
    }
}
