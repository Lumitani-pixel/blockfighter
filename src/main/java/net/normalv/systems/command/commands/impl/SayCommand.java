package net.normalv.systems.command.commands.impl;

import net.minecraft.text.Text;
import net.normalv.BlockFighter;
import net.normalv.systems.command.Arg;
import net.normalv.systems.command.ArgValues;
import net.normalv.systems.command.commands.Command;

public class SayCommand extends Command {
    public SayCommand() {
        super("say", new Arg<>("content", String.class));
    }

    @Override
    public void run(ArgValues args) {
        BlockFighter.textManager.sendTextServerSide(Text.literal(args.get("content")));
    }
}
