package xyz.lunalna.modifierkeybinds.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lunalna.modifierkeybinds.KeyChord;
import xyz.lunalna.modifierkeybinds.ModifierKeybinds;
import xyz.lunalna.modifierkeybinds.client.Screens;

//? if >=1.21.11 {
//?}
//? if <1.17 {
/*import net.minecraft.client.gui.screens.controls.ControlsScreen;
 */
//?} else if <1.20.5 {
/*import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
 */
//?} else {
//?}

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    //? if >=1.21.11 {
    @Inject(method = "matches(Lnet/minecraft/client/input/KeyEvent;)Z", at = @At("HEAD"), cancellable = true)
    private void modifierKeybinds$matchChord(KeyEvent event, CallbackInfoReturnable<Boolean> callback) {
        matchChord(event.key(), event.modifiers(), callback);
    }
    //?} else {
    /*@Inject(method = "matches(II)Z", at = @At("HEAD"), cancellable = true)
    private void modifierKeybinds$matchChord(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> callback) {
        matchChord(keyCode, ModifierKeybinds.getEventModifiers(), callback);
    }
    *///?}

    //? if >=26.2 {
    @Inject(method = "matches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z", at = @At("HEAD"), cancellable = true)
    private void modifierKeybinds$matchGlobalChord(InputConstants.Key key, CallbackInfoReturnable<Boolean> callback) {
        KeyChord chord = ModifierKeybinds.getBinding((KeyMapping) (Object) this);
        if (chord != null) {
            callback.setReturnValue(key.getType() == InputConstants.Type.KEYSYM
                    && chord.matches(key.getValue(), ModifierKeybinds.getEventModifiers()));
        }
    }
    //?}

    private void matchChord(int keyCode, int modifiers, CallbackInfoReturnable<Boolean> callback) {
        KeyChord chord = ModifierKeybinds.getBinding((KeyMapping) (Object) this);
        if (chord != null) {
            callback.setReturnValue(chord.matches(keyCode, modifiers));
        }
    }

    @Inject(method = "getTranslatedKeyMessage", at = @At("RETURN"), cancellable = true)
    private void modifierKeybinds$showChords(CallbackInfoReturnable<Component> callback) {
        KeyMapping mapping = (KeyMapping) (Object) this;
        KeyChord chord = ModifierKeybinds.getBinding(mapping);
        if (chord == null) {
            return;
        }
        //? if >=1.19 {
        callback.setReturnValue(Component.literal(chord.displayName()));
        //?} else {
        /*callback.setReturnValue(new net.minecraft.network.chat.TextComponent(chord.displayName()));
         *///?}
    }

    @Inject(method = "isDefault", at = @At("RETURN"), cancellable = true)
    private void modifierKeybinds$includeChordsInDefaultState(CallbackInfoReturnable<Boolean> callback) {
        if (ModifierKeybinds.getBinding((KeyMapping) (Object) this) != null) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "isUnbound", at = @At("RETURN"), cancellable = true)
    private void modifierKeybinds$includeChordBoundState(CallbackInfoReturnable<Boolean> callback) {
        if (ModifierKeybinds.getBinding((KeyMapping) (Object) this) != null) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "setKey", at = @At("HEAD"))
    private void modifierKeybinds$clearChordsOnReset(InputConstants.Key key, CallbackInfo callback) {
        KeyMapping mapping = (KeyMapping) (Object) this;
        //? if <1.17 {
        /*if (Screens.current() instanceof ControlsScreen
         *///?} else {
        if (Screens.current() instanceof KeyBindsScreen
                //?}
                && (!key.equals(InputConstants.UNKNOWN) || key.equals(mapping.getDefaultKey()))) {
            ModifierKeybinds.clearBinding(mapping);
        }
    }

    @Inject(method = "same", at = @At("HEAD"), cancellable = true)
    private void modifierKeybinds$detectChordConflict(KeyMapping other, CallbackInfoReturnable<Boolean> callback) {
        KeyChord chord = ModifierKeybinds.getBinding((KeyMapping) (Object) this);
        KeyChord otherChord = ModifierKeybinds.getBinding(other);
        if (chord != null || otherChord != null) {
            callback.setReturnValue(chord != null && chord.equals(otherChord));
        }
    }

    @Inject(method = "releaseAll", at = @At("HEAD"))
    private static void modifierKeybinds$releaseChords(CallbackInfo callback) {
        ModifierKeybinds.releaseActiveBindings();
    }
}
