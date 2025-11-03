package net.normalv.systems.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolCategoryComponent extends Component{
    private Tool.Category category;
    private List<ToolComponent> toolComponents = new ArrayList<>();
    private boolean isExpanded = false;
    private int targetHeight;

    public ToolCategoryComponent(int x, int y, int width, int height, Tool.Category category) {
        super(x, y, width, height);
        this.targetHeight = height;
        this.category = category;
        int startY = y+25;

        for(Tool tool : BlockFighter.toolManager.getToolsByCategory(category)) {
            toolComponents.add(new ToolComponent(x+5, startY, width-10, 20, tool));
            startY+=25;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int currentY = y + 20;

        if(isExpanded) setTargetHeight();
        ctx.fill(x, y, x+width, y+targetHeight, isMouseOver(mouseX, mouseY)?new Color(100, 100, 230).hashCode():new Color(80, 80, 230).hashCode());
        ctx.drawCenteredTextWithShadow(mc.textRenderer, category.getName(), width/2+x, y+5, Color.WHITE.hashCode());
        if(isExpanded) {
            for (ToolComponent toolComponent : toolComponents) {
                toolComponent.setY(currentY);
                toolComponent.render(ctx, mouseX, mouseY, delta);
                currentY += toolComponent.getTargetHeight() + 5;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY) && !isExpanded) {
            setTargetHeight();
            isExpanded = true;
            return true;
        } else if(isMouseOver(mouseX, mouseY) && isExpanded) {
            targetHeight=height;
            isExpanded = false;
            return true;
        }

        for(ToolComponent toolComponent : toolComponents) {
            if(toolComponent.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        for(ToolComponent toolComponent : toolComponents) {
            if(toolComponent.keyPressed(input)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(CharInput input) {
        for(ToolComponent toolComponent : toolComponents) {
            if(toolComponent.charTyped(input)) return true;
        }
        return super.charTyped(input);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(ToolComponent toolComponent : toolComponents) {
            if(toolComponent.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    public void setTargetHeight() {
        int sum = 0;
        for(ToolComponent toolComponent : toolComponents) {
            sum+=toolComponent.getTargetHeight();
        }
        targetHeight=height+sum+10;
    }

    public int getTargetHeight() {
        return targetHeight;
    }
}
