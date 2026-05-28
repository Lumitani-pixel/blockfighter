package net.normalv.event.events.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
