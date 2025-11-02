package net.normalv.systems.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.normalv.systems.tools.Tool;
import net.normalv.util.enums.ComponentCategory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryComponent extends Component{
    private ComponentCategory category;
    private List<Component> components = new ArrayList<>();

    public CategoryComponent(int x, int y, int width, int height, ComponentCategory category) {
        super(x, y, width, height);
        this.category = category;

        init();
    }

    @Override
    protected void init() {
        if(category==ComponentCategory.TOOLS) {
            int startY = y+25;

            for(Tool.Category toolCategory : Tool.Category.values()) {
                components.add(new ToolCategoryComponent(x+5, startY, width-10, 20, toolCategory));
                startY+=25;
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(x, y, x+width, y+height, new Color(70, 60, 200, 180).hashCode());
        ctx.fill(x, y, x+width, y+20, isMouseOver(mouseX, mouseY)?new Color(100, 100, 180).hashCode():new Color(80, 80, 180).hashCode());
        ctx.drawCenteredTextWithShadow(mc.textRenderer, category.getName(), width/2+x, y+5, Color.WHITE.hashCode());

        for(Component component : components) {
            component.render(ctx, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + 20;
    }
}