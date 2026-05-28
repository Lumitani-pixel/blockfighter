package net.normalv.util;

import net.minecraft.client.Minecraft;
import net.normalv.event.system.EventBus;
import net.normalv.systems.tools.setting.EnumUtils;

import java.util.Random;

public interface Util {
    Minecraft mc = Minecraft.getInstance();
    EnumUtils enumUtils = new EnumUtils();
    Random jrandom = new Random();
    EventBus EVENT_BUS = new EventBus();
}
