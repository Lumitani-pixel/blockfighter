package net.normalv;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.normalv.systems.fightbot.FightBot;
import net.normalv.systems.hud.HudRegistry;
import net.normalv.systems.managers.*;
import net.normalv.util.client.SoundUtils;
import net.normalv.util.interfaces.Util;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockFighter implements ModInitializer, ClientModInitializer, Util {
	public static final String MOD_ID = "blockfighter";
    public static final String MOD_NAME = "["+ BlockFighter.MOD_ID.toUpperCase()+"]";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(BlockFighter.MOD_ID, "binds"));

    public static KeyBinding guiBinding;
    public static KeyBinding toggleBinding;

    public static TextManager textManager;
    public static PlayerManager playerManager;
    public static TargetManager targetManager;
    public static WorldManager worldManager;
    public static ToolManager toolManager;
    public static EventManager eventManager;

    public static FightBot fightBot;

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

        hudRegistry = new HudRegistry();
	}

    @Override
    public void onInitializeClient() {
        guiBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key."+ BlockFighter.MOD_ID+".gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY
        ));
        toggleBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key."+ BlockFighter.MOD_ID+".toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_COMMA,
                CATEGORY
        ));

        toolManager.init();
        hudRegistry.register();
        SoundUtils.initialize();
    }

    public static boolean isInGame() {
        return mc.world!=null && mc.player != null;
    }
}