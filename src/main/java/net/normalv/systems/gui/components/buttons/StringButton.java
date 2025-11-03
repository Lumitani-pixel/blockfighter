package net.normalv.systems.gui.components.buttons;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.normalv.systems.tools.setting.Setting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class StringButton extends Button{
    private Setting<String> setting;
    private boolean isSelected = false;
    private String tempString = "";

    public StringButton(int x, int y, int width, int height, Setting<String> setting) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(mc.textRenderer, isSelected?tempString+"_":setting.getName()+" : "+setting.getValue(), width/2+x, y+5, Color.WHITE.hashCode());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY) && !isSelected) {
            isSelected = true;
            return true;
        } else if(isSelected && !isMouseOver(mouseX, mouseY)) {
            setting.setValue(tempString);
            tempString = "";
            isSelected = false;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if(input.isEnter()) {
            setting.setValue(tempString);
            tempString = "";
            isSelected = false;
            return true;
        } else if(input.key() == GLFW.GLFW_KEY_BACKSPACE && isSelected) {
            if(!tempString.isEmpty()) tempString=tempString.substring(0, tempString.length()-1);
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(CharInput input) {
        if(isSelected) {
            tempString+=input.asString();
            return true;
        }
        return super.charTyped(input);
    }
}
