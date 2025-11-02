package net.normalv.systems.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolCategoryComponent extends Component{
    private Tool.Category category;
    private List<ToolComponent> toolComponents = new ArrayList<>();

    public ToolCategoryComponent(int x, int y, int width, int height, Tool.Category category) {
        super(x, y, width, height);
        this.category = category;
        int startY = y+25;

        for(Tool tool : BlockFighter.toolManager.getToolsByCategory(category)) {
            toolComponents.add(new ToolComponent(x+5, startY, width-10, 20, tool));
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(x, y, x+width, y+height, isMouseOver(mouseX, mouseY)?new Color(100, 100, 230).hashCode():new Color(80, 80, 230).hashCode());
        ctx.drawCenteredTextWithShadow(mc.textRenderer, category.getName(), width/2+x, y+5, Color.WHITE.hashCode());
    }
}
