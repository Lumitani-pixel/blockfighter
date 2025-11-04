package net.normalv.systems.fightbot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.normalv.BlockFighter;
import net.normalv.util.interfaces.Util;

import java.util.ArrayList;
import java.util.List;

public class FightBot implements Util {
    public static final List<Item> swordItems = new ArrayList<>();
    private boolean isEnabled = false;

    public void onTick() {
        if(!BlockFighter.isInGame()) return;
    }

    public void onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
    }

    public void onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, HitResult hitResult) {
    }

    private void enable(){
        BlockFighter.textManager.sendTextClientSide(Text.literal("FightBot enabled"));
        isEnabled = true;
    }

    private void disable() {
        BlockFighter.textManager.sendTextClientSide(Text.literal("FightBot disabled"));
        isEnabled = false;
    }

    public void toggle() {
        if(!isEnabled) enable();
        else disable();
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
