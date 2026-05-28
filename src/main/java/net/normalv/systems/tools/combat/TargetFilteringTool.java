package net.normalv.systems.tools.combat;

import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

public class TargetFilteringTool extends Tool {
    Setting<Boolean> players, hostiles, animals;

    public TargetFilteringTool() {
        super("TargetFiltering", "What entities should be targeted", Category.COMBAT);
    }

    @Override
    public void registerSettings() {
        players = bool("players", true);
        hostiles = bool("hostiles", true);
        animals = bool("animals", false);
    }

    @Override
    public void onSettingChange() {
        BlockFighter.targetManager.allowPlayer = players.getValue();
        BlockFighter.targetManager.allowHostiles = hostiles.getValue();
        BlockFighter.targetManager.allowAnimals = animals.getValue();
    }
}
