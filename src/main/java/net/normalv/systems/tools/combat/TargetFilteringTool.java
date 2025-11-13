package net.normalv.systems.tools.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class TargetFilteringTool extends Tool {
    Setting<Boolean> players, hostiles, friendlies, allLiving;

    public TargetFilteringTool() {
        super("TargetFiltering", "What entities should be targeted", Category.COMBAT);
    }

    @Override
    public void registerSettings() {
        players = bool("players", true);
        hostiles = bool("hostiles", true);
        friendlies = bool("friendly-mobs", false);
        allLiving = bool("all-living-mobs", false);

        BlockFighter.targetManager.setPermittedEntities(List.of(PlayerEntity.class, HostileEntity.class));
    }

    @Override
    public void onSettingChange() {
        List<Class<? extends Entity>> entities = new ArrayList<>();
        if(players.getValue()) entities.add(PlayerEntity.class);
        if(hostiles.getValue()) entities.add(HostileEntity.class);
        if(friendlies.getValue()) entities.add(AnimalEntity.class);
        if(allLiving.getValue()) entities.add(LivingEntity.class);

        BlockFighter.targetManager.setPermittedEntities(entities);
    }
}
