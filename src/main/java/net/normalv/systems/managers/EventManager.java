package net.normalv.systems.managers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;
import net.normalv.BlockFighter;
import net.normalv.systems.gui.screens.BlockFighterGui;

// TODO MAKE EVENTS USING EVENTBUS AND MIXINS FASTER MORE RELIABLE AND CLEANER
public class EventManager extends Manager{
    private boolean wasPressed = false;

    public void registerEvents() {
        EVENT_BUS.register(this);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleInput();
            if(!BlockFighter.isInGame()) return;
            if(BlockFighter.fightBot.isEnabled()) BlockFighter.fightBot.onTick();
            toolManager.toolTick();
            targetManager.update();
            worldManager.onTick();
        });

        //TODO: Find better options for ActionResult return
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            if(BlockFighter.fightBot.isEnabled()) BlockFighter.fightBot.onAttackBlock(playerEntity, world, hand, blockPos, direction);
            toolManager.onAttackBlock(playerEntity, world, hand, blockPos, direction);
            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if(BlockFighter.fightBot.isEnabled()) BlockFighter.fightBot.onAttackEntity(player, world, hand, entity, hitResult);
            toolManager.onAttackEntity(player, world, hand, entity, hitResult);
            return ActionResult.PASS;
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