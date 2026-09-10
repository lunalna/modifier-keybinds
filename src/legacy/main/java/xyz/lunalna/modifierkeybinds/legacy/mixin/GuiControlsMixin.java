package xyz.lunalna.modifierkeybinds.legacy.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lunalna.modifierkeybinds.legacy.KeyChord;
import xyz.lunalna.modifierkeybinds.legacy.ModifierKeybinds;

@Mixin(GuiControls.class)
public abstract class GuiControlsMixin {
    @Shadow
    public KeyBinding buttonId;
    @Shadow
    public long time;

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void modifierKeybinds$capture(char character, int code, CallbackInfo callback) {
        if (buttonId == null) return;
        if (code == Keyboard.KEY_ESCAPE || KeyChord.modifiers() == 0) {
            ModifierKeybinds.clear(buttonId);
            return;
        }
        if (!KeyChord.isModifier(code)) {
            buttonId.setKeyCode(0);
            ModifierKeybinds.set(buttonId, new KeyChord(code, KeyChord.modifiers()));
            buttonId = null;
            time = Minecraft.getSystemTime();
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        callback.cancel();
    }
}
