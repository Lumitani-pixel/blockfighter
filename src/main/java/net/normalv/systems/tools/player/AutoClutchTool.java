package net.normalv.systems.tools.player;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.Tool;
import net.normalv.util.MathUtil;

import static net.normalv.systems.fightbot.FightBot.WATER_SLOT;

public class AutoClutchTool extends Tool {
    private int minClutchDistance = 4;
    public boolean placedWater = false;
    private BlockPos clutchPos;

    public AutoClutchTool() {
        super("AutoClutch", "Tries to clutch with water", Category.PLAYER);
    }

    @Override
    public void onEnabled() {
        clutchPos = null;
    }

    @Override
    public void onDisabled() {
        if(placedWater) {
            placeWater();
        }
    }

    @Override
    public void onTick() {
        if(placedWater && mc.player.onGround()) {
            placeWater();
            return;
        }
        if(mc.player.onGround() ||
                mc.player.fallDistance <= minClutchDistance ||
                !mc.player.getInventory().getItem(WATER_SLOT).is(Items.WATER_BUCKET)) return;


        clutchPos = BlockFighter.playerManager.getGroundPos(mc.player);
        if(mc.player.getEyePosition().distanceToSqr(MathUtil.clamp(mc.player.getEyePosition(), new AABB(clutchPos))) <= BlockFighter.fightBot.getMaxReachSqr()) {
            placeWater();
        }
    }

    private void placeWater() {
        if(mc.player.getInventory().getSelectedSlot() != WATER_SLOT) BlockFighter.playerManager.switchSlot(WATER_SLOT);

        mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, MathUtil.clamp(mc.player.getEyePosition(), new AABB(clutchPos)));
        BlockFighter.interactionManager.singleUseItem();

        if(placedWater) clutchPos = null;
        placedWater = !placedWater;
    }
}
