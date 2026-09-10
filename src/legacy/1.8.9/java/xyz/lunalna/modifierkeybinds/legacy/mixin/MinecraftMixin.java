package xyz.lunalna.modifierkeybinds.legacy.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lunalna.modifierkeybinds.legacy.KeyChord;
import xyz.lunalna.modifierkeybinds.legacy.ModifierKeybinds;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Redirect(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I"))
    private int modifierKeybinds$directKey(KeyBinding key) {
        KeyChord chord = ModifierKeybinds.get(key);
        if (chord == null) return key.getKeyCode();
        return chord.matches(Keyboard.getEventKey()) ? chord.keyCode() : 0;
    }
}
