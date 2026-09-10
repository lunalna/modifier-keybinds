package xyz.lunalna.modifierkeybinds.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lunalna.modifierkeybinds.KeyChord;
import xyz.lunalna.modifierkeybinds.ModifierKeybinds;

//? if >=1.20.5 {
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
//?}
//? if <1.17 {
/*import net.minecraft.client.gui.screens.controls.ControlsScreen;
 */
//?} else if <1.20.5 {
/*import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
 */
//?} else {
//?}
//? if >=1.21.11 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Util;
//?} else {
/*import net.minecraft.Util;
 */
//?}
//? if <1.17 {
/*@Mixin(ControlsScreen.class)
 *///?} else {
@Mixin(KeyBindsScreen.class)
//?}
public abstract class KeyBindsScreenMixin {
    @Shadow
    public KeyMapping selectedKey;
    @Shadow
    public long lastKeySelection;
    //? if >=1.20.5 {
    @Shadow
    private KeyBindsList keyBindsList;
    //?}

    //? if >=1.21.11 {
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void modifierKeybinds$captureChord(KeyEvent event, CallbackInfoReturnable<Boolean> callback) {
        capture(event.key(), event.modifiers(), callback);
    }
    //?} else {
    /*@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void modifierKeybinds$captureChord(int keyCode, int scanCode, int modifiers,
                                               CallbackInfoReturnable<Boolean> callback) {
        capture(keyCode, modifiers, callback);
    }
    *///?}

    private void capture(int keyCode, int modifiers, CallbackInfoReturnable<Boolean> callback) {
        if (selectedKey == null || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (selectedKey != null) {
                ModifierKeybinds.clearBinding(selectedKey);
            }
            return;
        }
        if (KeyChord.isModifierKey(keyCode)) {
            callback.setReturnValue(true);
            return;
        }
        if (!KeyChord.hasModifiers(modifiers)) {
            ModifierKeybinds.clearBinding(selectedKey);
            return;
        }

        selectedKey.setKey(InputConstants.UNKNOWN);
        ModifierKeybinds.setBinding(selectedKey, new KeyChord(keyCode, modifiers));
        selectedKey = null;
        lastKeySelection = Util.getMillis();
        //? if >=1.20.5 {
        keyBindsList.resetMappingAndUpdateButtons();
        //?} else {
        /*KeyMapping.resetMapping();
         *///?}
        callback.setReturnValue(true);
    }

}
