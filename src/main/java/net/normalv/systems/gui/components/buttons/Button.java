package net.normalv.systems.gui.components.buttons;

import net.minecraft.client.gui.DrawContext;
import net.normalv.systems.gui.components.Component;

import java.awt.*;

public class Button extends Component {

    public Button(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(x, y, x+width, y+height, isMouseOver(mouseX, mouseY) ? new Color(100, 100, 230).hashCode() : new Color(50, 50, 180).hashCode());
    }
}