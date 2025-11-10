package net.normalv.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.normalv.event.events.impl.ChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.normalv.util.interfaces.Util.EVENT_BUS;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessageHook(String content, CallbackInfo ci) {
        ChatEvent chatEvent = new ChatEvent(content);
        EVENT_BUS.post(chatEvent);
        if(chatEvent.isCancelled()) ci.cancel();
    }
}
