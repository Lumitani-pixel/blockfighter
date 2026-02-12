package net.normalv.systems.managers;

import net.normalv.BlockFighter;
import net.normalv.util.Util;

public class Manager implements Util {
    EventManager eventManager = BlockFighter.eventManager;
    PlayerManager playerManager = BlockFighter.playerManager;
    TargetManager targetManager = BlockFighter.targetManager;
    TextManager textManager = BlockFighter.textManager;
    ToolManager toolManager = BlockFighter.toolManager;
    WorldManager worldManager = BlockFighter.worldManager;
}
