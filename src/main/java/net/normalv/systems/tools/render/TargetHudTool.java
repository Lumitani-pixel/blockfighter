package net.normalv.systems.tools.render;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;

public class TargetHudTool extends Tool {
    Setting<Boolean> renderPlayerHead;

    private Entity target;
    private PlayerListEntry entry;
    private static SkinTextures headTexture = null;

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

        if(renderPlayerHead.getValue() && target instanceof PlayerEntity) {
            entry = mc.getNetworkHandler().getPlayerListEntry(target.getName().getString());
            if(entry!=null) headTexture = entry.getSkinTextures();
        }
    }

    public static SkinTextures getHeadTexture() {
        return headTexture;
    }

    public boolean drawPlayerHead() {
        return renderPlayerHead.getValue();
    }
}
