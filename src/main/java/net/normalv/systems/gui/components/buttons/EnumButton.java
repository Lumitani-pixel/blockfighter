package net.normalv.systems.gui.components.buttons;

import net.minecraft.client.gui.DrawContext;
import net.normalv.systems.tools.setting.Setting;

import java.awt.*;

public class EnumButton extends Button{
    private Setting<Enum<?>> setting;

    public EnumButton(int x, int y, int width, int height, Setting<Enum<?>> setting) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(mc.textRenderer, setting.getName()+" : "+enumUtils.getFormatedName(setting.getValue()), width/2+x, y+2, Color.WHITE.hashCode());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY)) {
            setting.setValue(enumUtils.getNextEnum(setting.getValue()));
            return true;
        }
        return false;
    }
}
