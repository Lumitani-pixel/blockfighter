package net.normalv.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MountScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MountScreenHandler.class)
public interface MountScreenHandlerAccessor {
    @Accessor("mount")
    LivingEntity getMount();
}
