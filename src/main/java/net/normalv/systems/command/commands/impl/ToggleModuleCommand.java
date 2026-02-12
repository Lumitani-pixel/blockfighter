package net.normalv.systems.command.commands.impl;

import net.normalv.BlockFighter;
import net.normalv.systems.command.Arg;
import net.normalv.systems.command.ArgValues;
import net.normalv.systems.command.commands.Command;

public class ToggleModuleCommand extends Command {
    public ToggleModuleCommand() {
        super("togglemodule", new Arg<>("module", String.class));
    }

    @Override
    public void run(ArgValues args) {
        BlockFighter.toolManager.getToolByName(args.get("module")).toggle();
    }
}
