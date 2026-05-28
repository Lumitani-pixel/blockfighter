package net.normalv.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.normalv.event.events.impl.AttackBlockEvent;
import net.normalv.event.events.impl.AttackEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.normalv.util.Util.EVENT_BUS;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "startDestroyBlock", at = @At(value = "HEAD"))
    public void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        EVENT_BUS.post(new AttackBlockEvent(pos, direction));
    }

    @Inject(method = "attack", at = @At(value = "HEAD"))
    public void onAttackEntity(Player player, Entity target, CallbackInfo ci) {
        EVENT_BUS.post(new AttackEntityEvent(player, target));
    }
}
