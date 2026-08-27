package net.normalv.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.normalv.BlockFighter;
import net.normalv.event.system.EventBus;
import net.normalv.mixin.accessor.KeyMappingAccessor;
import net.normalv.systems.tools.setting.EnumUtils;

import javax.swing.text.BadLocationException;
import java.util.Random;

public interface Util {
    Minecraft mc = Minecraft.getInstance();
    EnumUtils enumUtils = new EnumUtils();
    Random jrandom = new Random();
    EventBus EVENT_BUS = new EventBus();

    default boolean isFood(Item item) {
        return item.components().has(DataComponents.FOOD) && item.components().has(DataComponents.CONSUMABLE);
    }

    default boolean useKey(KeyMapping keyMapping) {
        if(!BlockFighter.isInGame()) return false;
        KeyMappingAccessor accessor = (KeyMappingAccessor) keyMapping;
        accessor.setTimesPressed(accessor.getTimesPressed()+1);
        return true;
    }
}
