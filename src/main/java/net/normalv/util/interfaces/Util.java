package net.normalv.util.interfaces;

import net.minecraft.client.MinecraftClient;
import net.normalv.event.system.EventBus;
import net.normalv.systems.tools.setting.EnumUtils;

import java.util.Random;

public interface Util {
    MinecraftClient mc = MinecraftClient.getInstance();
    EnumUtils enumUtils = new EnumUtils();
    Random jrandom = new Random();
    EventBus EVENT_BUS = new EventBus();
}
