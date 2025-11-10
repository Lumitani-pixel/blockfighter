package net.normalv.systems.managers;

import net.normalv.BlockFighter;
import net.normalv.event.events.impl.TickEvent;
import net.normalv.event.system.Subscribe;
import net.normalv.systems.gui.screens.BlockFighterGui;

public class EventManager extends Manager{
    private boolean wasPressed = false;

    public EventManager() {
        EVENT_BUS.register(this);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        handleInput();
        if(!BlockFighter.isInGame()) return;
        if(BlockFighter.fightBot.isEnabled()) BlockFighter.fightBot.onTick();
        toolManager.toolTick();
        targetManager.update();
        worldManager.onTick();
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