package net.normalv.systems.tools.misc;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

// Tested Shield breaking mechanic with mace WORKING!!!!
public class TestTool extends Tool {
    public TestTool() {
        super("test", "testing the tools functions and blah blah blah", Category.MISC);
    }

    @Override
    public void onTick() {
        Entity target = BlockFighter.fightBot.getTarget();
        if(target == null) return;

        for(int i = 0; i<10; i++) {
            mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(target, false));
        }
    }
}