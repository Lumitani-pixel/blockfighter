package net.normalv.systems.tools.misc;

import net.minecraft.text.Text;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

public class TestTool extends Tool {
    private Setting<Boolean> coolBooleanSetting;
    private Setting<TestModuleEnum> coolEnumSetting;
    private Setting<String> coolStringSetting;
    private Setting<Double> coolDoubleSetting;
    private Setting<Integer> coolIntegerSetting;

    public TestTool() {
        super("test-tool", "testing the tools functions and blah blah blah", Category.MISC);
    }

    @Override
    public void registerSettings() {
        coolBooleanSetting = bool("TestBool", false);
        coolEnumSetting = mode("TestEnum", TestModuleEnum.TEST);
        coolStringSetting = str("TestString", "LMAFO");
        coolDoubleSetting = num("DoubleTest", 5.0, 0.0, 10.0);
        coolIntegerSetting = num("IntegerTest", 1, -10, 10);
    }

    @Override
    public void onEnabled() {
        BlockFighter.textManager.sendTextClientSide(Text.literal("Bool is turned to: "+(coolBooleanSetting.getValue()?"true":"false")));
        BlockFighter.textManager.sendTextClientSide(Text.literal("Enum is turned to: "+enumUtils.getFormatedName(coolEnumSetting.getValue())));
        BlockFighter.textManager.sendTextClientSide(Text.literal("String is turned to: "+coolStringSetting.getValue()));
        BlockFighter.textManager.sendTextClientSide(Text.literal("Double is turned to: "+coolDoubleSetting.getValue()));
        BlockFighter.textManager.sendTextClientSide(Text.literal("Integer is turned to: "+coolIntegerSetting.getValue()));
    }

    public enum TestModuleEnum{
        TEST,
        NORMALV,
        KOSO
    }
}