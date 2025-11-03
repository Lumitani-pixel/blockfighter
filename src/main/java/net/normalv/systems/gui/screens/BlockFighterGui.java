package net.normalv.systems.gui.screens;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.normalv.BlockFighter;
import net.normalv.systems.gui.components.CategoryComponent;
import net.normalv.systems.gui.components.Component;
import net.normalv.systems.tools.Tool;
import net.normalv.util.enums.ComponentCategory;
import net.normalv.util.interfaces.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlockFighterGui extends Screen implements Util {
    private static BlockFighterGui instance;
    private static boolean isAssigningKey = false;
    private int categoryWidth;
    private List<CategoryComponent> categoryComponents = new ArrayList<>();

    static {
        instance = new BlockFighterGui();
    }

    public BlockFighterGui() {
        super(Text.literal("Block Fighter GUI"));
    }

    @Override
    protected void init() {
        super.init();

        categoryComponents.clear(); // rebuild if needed

        categoryWidth = (width / ComponentCategory.values().length) - 40;
        int startX = 25;

        for (ComponentCategory category : ComponentCategory.values()) {
            categoryComponents.add(new CategoryComponent(startX,
                    50,
                    categoryWidth,
                    category == ComponentCategory.TOOLS ? Tool.Category.values().length*20-50 : 140,
                    category)
            );
            startX += categoryWidth + 25;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawCenteredTextWithShadow(mc.textRenderer, title, width/2, 20, Color.WHITE.hashCode());

        categoryComponents.forEach(categoryComponent -> {
            categoryComponent.render(context, mouseX, mouseY, deltaTicks);
        });
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if(isAssigningKey) {
            BlockFighter.textManager.sendTextClientSide(Text.literal("Assigned new key for gui : ").append(Text.literal(input.toString())));
            return true;
        } else if(BlockFighter.guiBinding.matchesKey(input)) {
            close();
            return true;
        }
        for(CategoryComponent component : categoryComponents) {
            if(component.keyPressed(input)) return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseReleased(Click click) {
        for(CategoryComponent categoryComponent : categoryComponents) {
            if(categoryComponent.mouseReleased(click.x(), click.y(), click.button())) return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean charTyped(CharInput input) {
        for(CategoryComponent categoryComponent : categoryComponents) {
            if(categoryComponent.charTyped(input)) return true;
        }
        return super.charTyped(input);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        categoryComponents.forEach(categoryComponent -> {
            categoryComponent.mouseClicked(click.x(), click.y(), click.button());
        });
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static BlockFighterGui getInstance() {
        if(instance==null) instance = new BlockFighterGui();
        return instance;
    }

    public static boolean isAssigningKey() {
        return isAssigningKey;
    }

     public static void setAssigningKey(boolean assigningKey) {
        isAssigningKey = assigningKey;
    }
}
