package xyz.lunalna.modifierkeybinds.legacy.mixin;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lunalna.modifierkeybinds.legacy.ModifierKeybinds;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Inject(method = "setKeyCode", at = @At("HEAD"))
    private void modifierKeybinds$clear(int code, CallbackInfo callback) {
        ModifierKeybinds.clear((KeyBinding) (Object) this);
    }

    @Inject(method = "setKeyBindState", at = @At("HEAD"), cancellable = true)
    private static void modifierKeybinds$state(int code, boolean down, CallbackInfo callback) {
        if (ModifierKeybinds.keyEvent(code, down)) callback.cancel();
    }

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private static void modifierKeybinds$tick(int code, CallbackInfo callback) {
        if (ModifierKeybinds.consumed(code)) callback.cancel();
    }

    @Inject(method = "unPressAllKeys", at = @At("HEAD"))
    private static void modifierKeybinds$release(CallbackInfo callback) {
        ModifierKeybinds.releaseAll();
    }
}
