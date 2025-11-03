package net.normalv.util.interfaces;

import net.minecraft.client.MinecraftClient;
import net.normalv.systems.tools.setting.EnumUtils;

public interface Util {
    MinecraftClient mc = MinecraftClient.getInstance();
    EnumUtils enumUtils = new EnumUtils();
}
