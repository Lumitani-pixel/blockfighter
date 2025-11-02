package net.normalv.systems.gui.components;

import net.normalv.systems.tools.Tool;

public class ToolComponent extends Component{
    private Tool tool;

    public ToolComponent(int x, int y, int width, int height, Tool tool) {
        super(x, y, width, height);
        this.tool = tool;
    }
}
