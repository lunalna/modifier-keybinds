package xyz.lunalna.modifierkeybinds.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lunalna.modifierkeybinds.ModifierKeybinds;

//? if >=1.21.11 {
import net.minecraft.client.input.KeyEvent;
 //?}

@Mixin(KeyboardHandler.class)
public final class KeyboardHandlerMixin {
    @Unique
    private boolean modifierKeybinds$consumed;

    //? if >=1.21.11 {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void modifierKeybinds$handleChord(long window, int action, KeyEvent event, CallbackInfo callback) {
        handle(window, event.key(), action, event.modifiers());
    }
    //?} else {
    /*@Inject(method = "keyPress", at = @At("HEAD"))
    private void modifierKeybinds$handleChord(long window, int keyCode, int scanCode, int action, int modifiers,
                                              CallbackInfo callback) {
        handle(window, keyCode, action, modifiers);
    }
    *///?}

    private void handle(long window, int keyCode, int action, int modifiers) {
        //? if >=1.21.11 {
        long currentWindow = Minecraft.getInstance().getWindow().handle();
        //?} else {
        /*long currentWindow = Minecraft.getInstance().getWindow().getWindow();
         *///?}
        modifierKeybinds$consumed = false;
        if (window != currentWindow) {
            return;
        }
        ModifierKeybinds.setEventModifiers(modifiers);
        modifierKeybinds$consumed = keyCode != InputConstants.UNKNOWN.getValue()
                && ModifierKeybinds.onKeyEvent(keyCode, action, modifiers);
    }

    // suppress the default bind if we consumed it
    @Inject(method = "keyPress", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"),
            cancellable = true)
    private void modifierKeybinds$suppressPlainKey(CallbackInfo callback) {
        if (modifierKeybinds$consumed) {
            callback.cancel();
        }
    }
}
