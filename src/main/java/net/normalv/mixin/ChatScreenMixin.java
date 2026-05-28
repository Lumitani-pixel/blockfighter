package net.normalv.mixin;

import net.minecraft.client.gui.screens.ChatScreen;
import net.normalv.event.events.impl.ChatEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.normalv.util.Util.EVENT_BUS;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, boolean addToHistory, CallbackInfo ci) {
        ChatEvent event = new ChatEvent(message);
        EVENT_BUS.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
