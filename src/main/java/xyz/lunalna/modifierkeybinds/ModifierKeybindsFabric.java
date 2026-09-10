package xyz.lunalna.modifierkeybinds;

//? if fabric {

import net.fabricmc.api.ClientModInitializer;

public final class ModifierKeybindsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModifierKeybinds.init();
    }
}
//?}
