package net.normalv.systems.gui.components.buttons;

import net.minecraft.client.gui.DrawContext;
import net.normalv.systems.tools.setting.Setting;

import java.awt.*;

public class BoolButton extends Button{
    private Setting<Boolean> setting;

    public BoolButton(int x, int y, int width, int height, Setting<Boolean> setting) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(mc.textRenderer, setting.getName(), width/2+x, y+5, setting.getValue()?Color.GREEN.hashCode():Color.RED.hashCode());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY)) {
            setting.setValue(!setting.getValue());
            return true;
        }
        return false;
    }
}
