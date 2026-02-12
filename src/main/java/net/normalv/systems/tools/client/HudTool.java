package net.normalv.systems.tools.client;

import net.normalv.systems.hud.hudcomponents.ToolHud;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

public class HudTool extends Tool {
    Setting<Boolean> rainbow;
    Setting<Float> rainbowSpeed;

    public HudTool() {
        super("HUD", "Settings for the hud display", Category.CLIENT);
    }

    @Override
    public void registerSettings() {
        rainbow = bool("rainbow", false);
        rainbowSpeed = num("rainbow-speed", 0.3f, 0.0f, 1.0f);
    }

    @Override
    public void onSettingChange() {
        ToolHud.setRainbow(rainbow.getValue());
        ToolHud.setColorCycleSpeed(rainbowSpeed.getValue());
    }
}