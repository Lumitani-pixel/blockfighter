package net.normalv;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.normalv.pathing.PathFinder;
import net.normalv.systems.command.CommandExecutor;
import net.normalv.systems.fightbot.FightBot;
import net.normalv.systems.hud.HudRegistry;
import net.normalv.systems.managers.*;
import net.normalv.util.client.SoundUtils;
import net.normalv.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockFighter implements ModInitializer, ClientModInitializer, Util {
	public static final String MOD_ID = "blockfighter";
    public static final String MOD_NAME = "["+ BlockFighter.MOD_ID.toUpperCase()+"]";
    public static final char PREFIX = '$';
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static TextManager textManager;
    public static PlayerManager playerManager;
    public static TargetManager targetManager;
    public static WorldManager worldManager;
    public static ToolManager toolManager;
    public static EventManager eventManager;

    public static FightBot fightBot;
    public static PathFinder pathFinder;

    public static CommandExecutor commandExecutor;

    private static HudRegistry hudRegistry;

	@Override
	public void onInitialize() {
		LOGGER.info("Welcome to "+MOD_ID+" a not so friendly bot!");

        textManager = new TextManager();
        playerManager = new PlayerManager();
        targetManager = new TargetManager();
        worldManager = new WorldManager();
        toolManager = new ToolManager();
        eventManager = new EventManager();

        fightBot = new FightBot();
        pathFinder = new PathFinder();

        commandExecutor = new CommandExecutor();

        hudRegistry = new HudRegistry();
	}

    @Override
    public void onInitializeClient() {
        toolManager.init();
        hudRegistry.register();
        SoundUtils.initialize();
    }

    public static boolean isInGame() {
        return mc.world!=null && mc.player != null;
    }
}