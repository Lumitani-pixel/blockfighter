package net.normalv.systems.gui.components;

import net.normalv.systems.gui.components.buttons.Button;
import net.normalv.systems.tools.Tool;

import java.util.ArrayList;
import java.util.List;

public class ToolComponent extends Component{
    private Tool tool;
    private List<Button> buttons = new ArrayList<>();

    public ToolComponent(int x, int y, int width, int height, Tool tool) {
        super(x, y, width, height);
        this.tool = tool;
    }
}
