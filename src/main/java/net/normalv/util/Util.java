package net.normalv.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.normalv.event.system.EventBus;
import net.normalv.systems.tools.setting.EnumUtils;

import java.util.Random;

public interface Util {
    Minecraft mc = Minecraft.getInstance();
    EnumUtils enumUtils = new EnumUtils();
    Random jrandom = new Random();
    EventBus EVENT_BUS = new EventBus();

    default boolean isFood(Item item) {
        return item.components().has(DataComponents.FOOD) && item.components().has(DataComponents.CONSUMABLE);
    }
}
