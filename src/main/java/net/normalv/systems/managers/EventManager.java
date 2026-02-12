package net.normalv.systems.managers;

import net.normalv.BlockFighter;
import net.normalv.event.events.impl.TickEvent;
import net.normalv.event.system.Subscribe;

public class EventManager extends Manager{
    public EventManager() {
        EVENT_BUS.register(this);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if(!BlockFighter.isInGame()) return;
        if(BlockFighter.fightBot.isEnabled()) BlockFighter.fightBot.onTick();
        toolManager.toolTick();
        targetManager.update();
        worldManager.onTick();
        BlockFighter.pathFinder.update();
    }
}