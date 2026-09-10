package xyz.lunalna.modifierkeybinds.legacy.mixin;

import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lunalna.modifierkeybinds.legacy.ModifierKeybinds;

@Mixin(GuiKeyBindingList.KeyEntry.class)
public abstract class KeyEntryMixin {
    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I"))
    private int modifierKeybinds$bound(KeyBinding key) {
        return ModifierKeybinds.get(key) == null ? key.getKeyCode() : Integer.MIN_VALUE;
    }
}
