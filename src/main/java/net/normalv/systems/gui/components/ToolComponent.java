package net.normalv.systems.gui.components;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.normalv.systems.gui.components.buttons.*;
import net.normalv.systems.gui.components.buttons.Button;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolComponent extends Component{
    private Tool tool;
    private List<Button> buttons = new ArrayList<>();
    private boolean isExpanded = false;
    private int targetHeight;

    public ToolComponent(int x, int y, int width, int height, Tool tool) {
        super(x, y, width, height);
        this.targetHeight = height;
        this.tool = tool;
        init();
    }

    @Override
    protected void init() {
        int startY = y;

        for (Setting<?> setting : tool.getSettings()) {
            startY += 22;

            if (setting.isBooleanSetting()) {
                buttons.add(new BoolButton(x + 10, startY, width - 20, 20, (Setting<Boolean>) setting));
            } else if (setting.isNumberSetting()) {
                buttons.add(new NumberButton(x + 10, startY, width - 20, 20, (Setting<Number>) setting));
            } else if (setting.isStringSetting()) {
                buttons.add(new StringButton(x + 10, startY, width - 20, 20, (Setting<String>) setting));
            } else if (setting.isEnumSetting()) {
                buttons.add(new EnumButton(x + 10, startY, width - 20, 20, (Setting<Enum<?>>) setting));
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(x, y, x+width, y+targetHeight, isMouseOver(mouseX, mouseY)?new Color(80, 80, 180).hashCode():new Color(50, 50, 180).hashCode());
        ctx.drawCenteredTextWithShadow(mc.textRenderer, Text.literal(tool.getName()+" ").append(tool.isEnabled()?Text.literal("✔").formatted(Formatting.GREEN):Text.literal("✖").formatted(Formatting.RED)), width/2+x, y+5, Color.WHITE.hashCode());
        if (isExpanded) {
            int startY = y + height + 2;

            for (Button button : buttons) {
                button.setY(startY);
                button.render(ctx, mouseX, mouseY, delta);
                startY += button.getHeight() + 2;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(isMouseOver(mouseX, mouseY) && mouseButton==0) {
            tool.toggle();
        } else if(isMouseOver(mouseX, mouseY) && !isExpanded && mouseButton==1) {
            targetHeight+=buttons.size()*22+5;
            isExpanded = true;
            return true;
        } else if(isMouseOver(mouseX, mouseY) && isExpanded && mouseButton==1) {
            targetHeight=height;
            isExpanded = false;
            return true;
        }

        if(isExpanded) {
            for(Button button : buttons) {
                if(button.mouseClicked(mouseX, mouseY, mouseButton)){
                    tool.onSettingChange();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        for(Button button : buttons) {
            if(button.keyPressed(input)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(CharInput input) {
        for(Button button : buttons) {
            if(button.charTyped(input)) return true;
        }
        return super.charTyped(input);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        for(Button button : buttons) {
            if(button.mouseReleased(mouseX, mouseY, mouseButton)) return true;
        }
        return false;
    }

    public int getTargetHeight() {
        return targetHeight;
    }
}
