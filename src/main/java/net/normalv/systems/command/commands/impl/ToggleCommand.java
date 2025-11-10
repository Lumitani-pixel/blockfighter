package net.normalv.systems.command.commands.impl;

import net.normalv.BlockFighter;
import net.normalv.systems.command.ArgValues;
import net.normalv.systems.command.commands.Command;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle");
    }

    @Override
    public void run(ArgValues args) {
        BlockFighter.fightBot.toggle();
    }
}
