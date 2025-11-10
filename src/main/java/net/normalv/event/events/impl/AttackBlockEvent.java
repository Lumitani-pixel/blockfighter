package net.normalv.event.events.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.normalv.event.events.Event;

public class AttackBlockEvent extends Event {
    private BlockPos pos;
    private Direction direction;

    public AttackBlockEvent(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Direction getDirection() {
        return direction;
    }
}
