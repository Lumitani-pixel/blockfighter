package net.normalv.systems.command.commands.impl;

import net.minecraft.network.chat.Component;
import net.normalv.BlockFighter;
import net.normalv.systems.command.Arg;
import net.normalv.systems.command.ArgValues;
import net.normalv.systems.command.commands.Command;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void run(ArgValues args) {
        BlockFighter.textManager.sendTextClientSide(Component.literal("Commands:"));
        for (Command cmd : BlockFighter.commandExecutor.getCommands()) {
            StringBuilder usage = new StringBuilder("$" + cmd.getName());
            for (Arg<?> arg : cmd.getArgs()) {
                usage.append(" <").append(arg.getName()).append(">");
            }
            BlockFighter.textManager.sendTextClientSide(Component.literal(usage.toString()));
        }
    }
}