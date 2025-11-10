package net.normalv.systems.command.commands;

import net.minecraft.text.Text;
import net.normalv.BlockFighter;
import net.normalv.systems.command.Arg;
import net.normalv.systems.command.ArgValues;

public abstract class Command {
    private final String name;
    private final Arg<?>[] args;

    public Command(String name, Arg<?>... args) {
        this.name = name;
        this.args = args;
    }

    public String getName() { return name; }
    public Arg<?>[] getArgs() { return args; }

    public abstract void run(ArgValues args);

    public ArgValues parseArgs(String[] input) {
        ArgValues result = new ArgValues();

        for (int i = 0; i < args.length; i++) {
            Arg<?> def = args[i];
            Object value;

            if (i < input.length) {
                try {
                    value = def.parse(input[i]);
                } catch (IllegalArgumentException e) {
                    BlockFighter.textManager.sendTextClientSide(
                            Text.literal("Invalid value for " + def.getName() + ": " + input[i])
                    );
                    return null;
                }
            } else if (def.getDefaultValue() != null) {
                value = def.getDefaultValue();
            } else {
                BlockFighter.textManager.sendTextClientSide(
                        Text.literal("Missing argument: " + def.getName())
                );
                return null;
            }

            result.put(def.getName(), value);
        }

        return result;
    }
}