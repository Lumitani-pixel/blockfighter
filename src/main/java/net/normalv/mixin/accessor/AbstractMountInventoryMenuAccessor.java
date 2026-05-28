package net.normalv.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;

@Mixin(AbstractMountInventoryMenu.class)
public interface AbstractMountInventoryMenuAccessor {
    @Accessor("mount")
    LivingEntity getMount();
}
