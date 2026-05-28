package net.normalv.systems.managers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

public class TextManager extends Manager{

    public Component getToggleMsg(Tool tool, boolean enabled) {
        return Component.literal(BlockFighter.MOD_NAME+" ").withStyle(ChatFormatting.DARK_GREEN)
                .append(Component.literal(tool.getName()+" ").withStyle(ChatFormatting.BLUE))
                .append(Component.literal(": ").withStyle(ChatFormatting.WHITE))
                .append(enabled ? Component.literal("On").withStyle(ChatFormatting.GREEN) : Component.literal("Off").withStyle(ChatFormatting.RED)
                );
    }

    public Component getInfoMsg(Tool tool, String info) {
        return Component.literal(BlockFighter.MOD_NAME+" ").withStyle(ChatFormatting.DARK_GREEN)
                .append(Component.literal(tool.getName()+" ").withStyle(ChatFormatting.BLUE))
                .append(Component.literal(info).withStyle(ChatFormatting.WHITE)
                );
    }

    public void sendTextClientSide(Component component) {
        assert mc.player != null;
        mc.player.sendSystemMessage(component);
    }

    public void sendTextServerSide(Component component) {
        mc.getConnection().sendChat(component.getString());
    }
}
