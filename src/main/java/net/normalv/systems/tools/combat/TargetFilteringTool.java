package net.normalv.systems.tools.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.normalv.BlockFighter;
import net.normalv.systems.managers.TargetManager;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

import java.util.ArrayList;
import java.util.List;

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
