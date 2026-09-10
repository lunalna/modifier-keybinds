package xyz.lunalna.modifierkeybinds.legacy.mixin;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lunalna.modifierkeybinds.legacy.KeyChord;
import xyz.lunalna.modifierkeybinds.legacy.ModifierKeybinds;

@Mixin(KeyBinding.class)
public abstract class ForgeKeyBindingMixin {
    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true, remap = false)
    private void modifierKeybinds$label(CallbackInfoReturnable<String> callback) {
        KeyChord chord = ModifierKeybinds.get((KeyBinding) (Object) this);
        if (chord != null) callback.setReturnValue(chord.displayName());
    }

    @Inject(method = "isSetToDefaultValue", at = @At("HEAD"), cancellable = true, remap = false)
    private void modifierKeybinds$default(CallbackInfoReturnable<Boolean> callback) {
        if (ModifierKeybinds.get((KeyBinding) (Object) this) != null) callback.setReturnValue(false);
    }

    @Inject(method = "isActiveAndMatches", at = @At("HEAD"), cancellable = true, remap = false)
    private void modifierKeybinds$match(int code, CallbackInfoReturnable<Boolean> callback) {
        KeyChord chord = ModifierKeybinds.get((KeyBinding) (Object) this);
        if (chord != null) callback.setReturnValue(chord.matches(code));
    }

    @Inject(method = "conflicts", at = @At("HEAD"), cancellable = true, remap = false)
    private void modifierKeybinds$conflict(KeyBinding other, CallbackInfoReturnable<Boolean> callback) {
        KeyChord chord = ModifierKeybinds.get((KeyBinding) (Object) this);
        KeyChord otherChord = ModifierKeybinds.get(other);
        if (chord != null || otherChord != null) callback.setReturnValue(chord != null && chord.equals(otherChord));
    }

    @Inject(method = "setKeyModifierAndCode", at = @At("HEAD"), remap = false)
    private void modifierKeybinds$clear(KeyModifier modifier, int code, CallbackInfo callback) {
        ModifierKeybinds.clear((KeyBinding) (Object) this);
    }
}
