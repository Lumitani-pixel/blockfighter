package net.normalv.systems.tools.render;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

public class TargetHudTool extends Tool {
    Setting<Boolean> renderPlayerHead;

    private Entity target;
    private PlayerInfo entry;
    private static PlayerSkin headTexture = null;

    public TargetHudTool() {
        super("TargetHud", "Displays info on the current target", Category.RENDER);
    }

    @Override
    public void registerSettings() {
        renderPlayerHead = bool("player-head", true);
    }

    @Override
    public void onTick() {
        target = BlockFighter.targetManager.getCurrentTarget();

        if(renderPlayerHead.getValue() && target instanceof Player) {
            entry = mc.getConnection().getPlayerInfo(target.getName().getString());
            if(entry!=null) headTexture = entry.getSkin();
        }
    }

    public static PlayerSkin getHeadTexture() {
        return headTexture;
    }

    public boolean drawPlayerHead() {
        return renderPlayerHead.getValue();
    }
}
