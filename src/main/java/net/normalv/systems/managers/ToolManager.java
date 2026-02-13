package net.normalv.systems.managers;

import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.client.HudTool;
import net.normalv.systems.tools.client.SoundTool;
import net.normalv.systems.tools.combat.AutoShieldTool;
import net.normalv.systems.tools.combat.TargetFilteringTool;
import net.normalv.systems.tools.misc.TestTool;
import net.normalv.systems.tools.render.TargetHudTool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ToolManager extends Manager{
    private static final List<Tool> tools = new ArrayList<>();

    public void init() {
        tools.add(new TestTool());
        tools.add(new HudTool());
        tools.add(new SoundTool());
        tools.add(new TargetHudTool());
        tools.add(new TargetFilteringTool());
        tools.add(new AutoShieldTool());

        tools.sort(Comparator.comparing(Tool::getName));
    }

    public Tool getToolByName(String name) {
        for(Tool tool : tools) {
            if(!tool.getName().equalsIgnoreCase(name)) continue;
            return tool;
        }
        return null;
    }

    public <T extends Tool> T getToolByClass(Class<T> clazz) {
        for(Tool tool : tools) {
            if(!clazz.isInstance(tool)) continue;
            return (T) tool;
        }
        return null;
    }

    public List<Tool> getToolsByCategory(Tool.Category category) {
        List<Tool> toolsInCategory = new ArrayList<>();
        for(Tool tool : tools) {
            if(tool.getCategory()!=category) continue;
            toolsInCategory.add(tool);
        }
        return toolsInCategory;
    }

    public List<Tool> getActivatedTools(boolean sortedByLength) {
        List<Tool> activatedTools = new ArrayList<>();
        for(Tool tool : tools) {
            if(!tool.isEnabled()) continue;
            activatedTools.add(tool);
        }
        return sortedByLength ? getSortedByLength(activatedTools) : activatedTools;
    }

    public List<Tool> getSortedByLength(List<Tool> unsortedTools) {
        unsortedTools.sort(Comparator.comparingInt(tool -> mc.textRenderer.getWidth(tool.getDisplayName())));
        return unsortedTools;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void toolTick() {
        if(!BlockFighter.isInGame()) return;
        for(Tool tool : tools){
            if(!tool.isEnabled()) continue;
            tool.onTick();
        }
    }
}
