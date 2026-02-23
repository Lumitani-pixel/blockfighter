package net.normalv.systems.tools.misc;

import net.normalv.systems.tools.Tool;

public class TestTool extends Tool {
    public TestTool() {
        super("test-tool", "testing the tools functions and blah blah blah", Category.MISC);
    }

    @Override
    public void onTick() {
    }
}