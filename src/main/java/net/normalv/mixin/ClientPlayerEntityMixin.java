package net.normalv.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.normalv.event.events.impl.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.normalv.util.interfaces.Util.EVENT_BUS;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void onTick(CallbackInfo ci) {
        EVENT_BUS.post(new TickEvent());
    }
}
