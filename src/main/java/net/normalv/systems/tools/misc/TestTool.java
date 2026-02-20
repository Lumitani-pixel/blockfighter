package net.normalv.systems.tools.misc;

import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

import static net.normalv.systems.fightbot.FightBot.SPEAR_SLOT;

public class TestTool extends Tool {
    public TestTool() {
        super("test-tool", "testing the tools functions and blah blah blah", Category.MISC);
    }

    @Override
    public void onTick() {
        if(mc.player.getInventory().getSelectedSlot() != SPEAR_SLOT) return;
        if(!mc.options.useKey.isPressed()) mc.options.useKey.setPressed(true);
        if(!mc.player.isUsingItem()) mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
    }
}