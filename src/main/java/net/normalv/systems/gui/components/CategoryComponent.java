package net.normalv.systems.gui.components;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.normalv.systems.tools.Tool;
import net.normalv.util.enums.ComponentCategory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryComponent extends Component{
    private ComponentCategory category;
    private List<Component> components = new ArrayList<>();
    private int targetHeight;

    public CategoryComponent(int x, int y, int width, int height, ComponentCategory category) {
        super(x, y, width, height);
        this.category = category;
        this.targetHeight = height;
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
        targetHeight=height+getSumHeightOfComponents();
        ctx.fill(x, y, x+width, y+targetHeight, new Color(70, 60, 200, 180).hashCode());
        ctx.fill(x, y, x+width, y+20, isMouseOver(mouseX, mouseY)?new Color(100, 100, 180).hashCode():new Color(80, 80, 180).hashCode());
        ctx.drawCenteredTextWithShadow(mc.textRenderer, category.getName(), width/2+x, y+5, Color.WHITE.hashCode());

        int startY = y + 25; // reset every frame
        for (Component component : components) {
            component.setY(startY); // dynamically reposition
            component.render(ctx, mouseX, mouseY, delta);
            startY += (component instanceof ToolCategoryComponent toolCat)
                    ? toolCat.getTargetHeight() + 5 // spacing between components
                    : 25;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(Component component : components) {
            if(component.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + 20;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        for(Component component : components) {
            if(component.keyPressed(input)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(CharInput input) {
        for(Component component : components) {
            if(component.charTyped(input)) return true;
        }
        return super.charTyped(input);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(Component component : components) {
            if(component.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    private int getSumHeightOfComponents() {
        int sum = 0;
        for(Component component : components) {
            if(component instanceof ToolCategoryComponent toolCategoryComponent) sum+=toolCategoryComponent.getTargetHeight();
        }
        return sum;
    }
}