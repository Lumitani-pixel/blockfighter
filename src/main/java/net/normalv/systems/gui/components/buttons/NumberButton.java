package net.normalv.systems.gui.components.buttons;

import net.minecraft.client.gui.DrawContext;
import net.normalv.systems.tools.setting.Setting;

import java.awt.*;

public class NumberButton extends Button {
    private final Setting<Number> setting;
    private boolean dragging = false;

    public NumberButton(int x, int y, int width, int height, Setting<Number> setting) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        ctx.fill(x, y + height / 2 - 2, x + width, y + height / 2 + 2, new Color(80, 80, 80).getRGB());

        double min = setting.getMinValue().doubleValue();
        double max = setting.getMaxValue().doubleValue();
        double value = setting.getValue().doubleValue();

        double percent = (value - min) / (max - min);
        int knobX = (int) (x + percent * width);

        // Knob
        ctx.fill(knobX - 3, y + 3, knobX + 3, y + height - 3, new Color(160, 160, 255).getRGB());

        String displayValue = setting.getValue() instanceof Integer
                ? String.valueOf(setting.getValue().intValue())
                : String.format("%.2f", setting.getValue().doubleValue());

        String label = setting.getName() + ": " + displayValue;
        ctx.drawCenteredTextWithShadow(mc.textRenderer, label, x + width / 2, y + 2, Color.WHITE.getRGB());

        if (dragging) {
            updateValue(mouseX);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && button == 0) {
            dragging = true;
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateValue(double mouseX) {
        double min = setting.getMinValue().doubleValue();
        double max = setting.getMaxValue().doubleValue();

        double percent = (mouseX - x) / width;
        percent = Math.max(0, Math.min(1, percent));

        double newValue = min + (max - min) * percent;

        if (setting.getValue() instanceof Integer) {
            setting.setValue((int) Math.round(newValue));
        } else if (setting.getValue() instanceof Float) {
            setting.setValue((float) newValue);
        } else {
            setting.setValue(newValue);
        }
    }
}