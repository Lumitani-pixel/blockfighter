package net.normalv.systems.command;

import net.minecraft.text.Text;
import net.normalv.BlockFighter;
import net.normalv.event.events.impl.ChatEvent;
import net.normalv.systems.command.commands.Command;
import net.normalv.systems.command.commands.impl.HelpCommand;
import net.normalv.systems.command.commands.impl.SayCommand;
import net.normalv.systems.command.commands.impl.ToggleCommand;
import net.normalv.util.interfaces.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandExecutor implements Util {
    private final List<Command> commands = new ArrayList<>();

    public CommandExecutor() {
        EVENT_BUS.register(this);
        registerCommands();
    }

    private void registerCommands() {
        commands.add(new HelpCommand());
        commands.add(new ToggleCommand());
        commands.add(new SayCommand());
    }

    public void onChatEvent(ChatEvent event) {
        String content = event.getContent();
        if (content.charAt(0) != BlockFighter.PREFIX) return;

        event.setCancelled(true);
        String[] parts = content.substring(1).split(" ");
        String name = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command command = findCommand(name);
        if (command == null) {
            BlockFighter.textManager.sendTextClientSide(Text.literal("Unknown command: " + name));
            return;
        }

        ArgValues parsed = command.parseArgs(args);
        if (parsed != null) {
            command.run(parsed);
        }
    }

    private Command findCommand(String name) {
        for (Command cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(name)) return cmd;
        }
        return null;
    }

    public List<Command> getCommands() { return commands; }
}