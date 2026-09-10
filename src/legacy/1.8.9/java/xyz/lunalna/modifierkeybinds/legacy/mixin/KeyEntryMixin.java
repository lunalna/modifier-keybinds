package xyz.lunalna.modifierkeybinds.legacy.mixin;

import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lunalna.modifierkeybinds.legacy.KeyChord;
import xyz.lunalna.modifierkeybinds.legacy.ModifierKeybinds;

@Mixin(GuiKeyBindingList.KeyEntry.class)
public abstract class KeyEntryMixin {
    @Shadow
    @Final
    private KeyBinding keybinding;

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;getKeyDisplayString(I)Ljava/lang/String;"))
    private String modifierKeybinds$label(int code) {
        KeyChord chord = ModifierKeybinds.get(keybinding);
        return chord == null ? GameSettings.getKeyDisplayString(code) : chord.displayName();
    }

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I"))
    private int modifierKeybinds$conflicts(KeyBinding key) {
        KeyChord chord = ModifierKeybinds.get(key);
        if (chord == null) return key.getKeyCode();
        KeyChord selected = ModifierKeybinds.get(keybinding);
        return chord.equals(selected) ? Integer.MIN_VALUE : Integer.MIN_VALUE + 1;
    }
}
