package net.normalv.systems.managers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.normalv.BlockFighter;
import net.normalv.systems.gui.screens.BlockFighterGui;

public class EventManager extends Manager{
    private boolean wasPressed = false;

    public void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleInput();
            if(BlockFighter.fightBot.isEnabled()) BlockFighter.fightBot.onTick();
            if(BlockFighter.isInGame()) {
                toolManager.toolTick();
                targetManager.update();
            }
        });
    }

    //TODO: Find a better way to listen to key input
    private void handleInput() {
        boolean guiPressed = BlockFighter.guiBinding.isPressed();
        boolean togglePressed = BlockFighter.toggleBinding.isPressed();

        // When key is first pressed
        if (!wasPressed && guiPressed) {
            mc.setScreen(BlockFighterGui.getInstance());
            wasPressed = true;
        } else if (!wasPressed && togglePressed) {
            BlockFighter.fightBot.toggle();
            wasPressed = true;
        }

        // When all keys are released
        if (!guiPressed && !togglePressed) {
            wasPressed = false;
        }
    }
}
